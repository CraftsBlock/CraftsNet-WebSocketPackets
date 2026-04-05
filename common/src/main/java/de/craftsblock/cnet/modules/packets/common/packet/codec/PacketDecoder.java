package de.craftsblock.cnet.modules.packets.common.packet.codec;

import de.craftsblock.cnet.modules.packets.common.WebSocketPackets;
import de.craftsblock.cnet.modules.packets.common.packet.Packet;
import de.craftsblock.cnet.modules.packets.common.packet.WrappedPacket;
import de.craftsblock.cnet.modules.packets.common.protocol.PacketBundle;
import de.craftsblock.craftscore.buffer.BufferUtil;

import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * Decodes incoming packet data from a {@link ByteBuffer} into {@link Packet} instances.
 * <p>
 * This decoder reads the bundle identifier and packet ID from the buffer,
 * validates packet size, and delegates to the corresponding {@link PacketBundle}
 * to reconstruct the packet. If no bundle matches the identifier, a
 * {@link WrappedPacket} is returned, encapsulating the raw data.
 * </p>
 * <p>
 * Packet size validation is performed against {@link PacketEncoder#MAX_PACKET_SIZE}
 * to prevent processing of oversized packets.
 * </p>
 *
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 1.0.0
 * @since 1.0.0
 */
public final class PacketDecoder {

    /**
     * Decodes a {@link Packet} from a {@link ByteBuffer}.
     * <p>
     * Reads the bundle identifier and packet ID from the buffer, checks for
     * oversized packets, and delegates to the appropriate {@link PacketBundle}
     * to reconstruct the packet. If no bundle is found, a {@link WrappedPacket}
     * is created.
     *
     * @param buffer The buffer containing the packet data.
     * @return The decoded {@link Packet}.
     * @throws IllegalStateException If the packet exceeds {@link PacketEncoder#MAX_PACKET_SIZE}.
     */
    public Packet decode(BufferUtil buffer) {
        WebSocketPackets webSocketPackets = WebSocketPackets.getInstanceSafely();

        String identifier = buffer.getUtf();
        PacketBundle packetBundle = webSocketPackets.getPacketBundleRegistry().getBundle(identifier);
        if (packetBundle == null) {
            return new WrappedPacket(buffer);
        }

        int id = buffer.getVarInt();

        int size = buffer.map(Buffer::capacity);
        if (size > PacketEncoder.MAX_PACKET_SIZE) {
            throw new IllegalStateException("Packet %s#%s exceeded max size! (Got: %s, Max: %s)".formatted(
                    identifier, id, size, PacketEncoder.MAX_PACKET_SIZE
            ));
        }

        BufferUtil packet = BufferUtil.of(buffer.getRaw().slice());
        return packetBundle.createPacket(id, packet);
    }

}
