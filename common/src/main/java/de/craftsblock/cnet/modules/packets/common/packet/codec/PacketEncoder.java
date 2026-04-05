package de.craftsblock.cnet.modules.packets.common.packet.codec;

import de.craftsblock.cnet.modules.packets.common.WebSocketPackets;
import de.craftsblock.cnet.modules.packets.common.packet.Packet;
import de.craftsblock.cnet.modules.packets.common.packet.WrappedPacket;
import de.craftsblock.cnet.modules.packets.common.protocol.PacketBundle;
import de.craftsblock.craftscore.buffer.BufferUtil;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Encodes {@link Packet} instances into {@link ByteBuffer byte buffers} for transmission
 * over a WebSocket connection.
 * <p>
 * This encoder handles both regular packets and {@link WrappedPacket}s. For
 * regular packets, it determines the appropriate {@link PacketBundle} to
 * retrieve the packet ID and bundle identifier. The resulting buffer includes
 * the bundle identifier, packet ID, and serialized packet data.
 * <p>
 * Packet size is validated against {@link #MAX_PACKET_SIZE} to prevent
 * oversized transmissions.
 *
 * @param webSocketPackets The WebSocketPackets instance for resolving packet
 *                         bundles and IDs, never {@code null}.
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 1.0.0
 * @see Packet
 * @see WrappedPacket
 * @see PacketBundle
 * @see ByteBuffer
 * @since 1.0.0
 */
public record PacketEncoder(WebSocketPackets webSocketPackets) {

    /**
     * Maximum allowed size of a packet in bytes (8 MB).
     */
    public static final int MAX_PACKET_SIZE = 8 * 1024 * 1024;

    /**
     * Encodes a {@link Packet} into a {@link ByteBuffer} for WebSocket transmission.
     * <p>
     * If the packet is a {@link WrappedPacket}, its pre-defined bundle and ID are used.
     * Otherwise, the packet is matched to its {@link PacketBundle} to determine its ID
     * and bundle identifier. The buffer begins with the bundle name and packet ID,
     * followed by the serialized packet data.
     *
     * @param packet The packet to encode.
     * @return A {@link ByteBuffer} containing the encoded packet data.
     * @throws IllegalStateException If the packet is unknown or exceeds {@link #MAX_PACKET_SIZE}.
     */
    public BufferUtil encode(Packet packet) {
        String bundle;
        int id;

        if (packet instanceof WrappedPacket wrapped) {
            bundle = wrapped.bundle();
            id = wrapped.id();
        } else {
            PacketBundle packetBundle = webSocketPackets.getPacketBundleRegistry().getBundle(packet);
            if (packetBundle == null || !packetBundle.containsPacket(packet)) {
                throw new IllegalStateException("Failed to encode a unknown packet %s".formatted(packet.getClass().getName()));
            }

            bundle = packetBundle.identifier();
            id = packetBundle.getId(packet);
        }

        BufferUtil buffer = BufferUtil.of(ByteBuffer.allocate(
                bundle.getBytes(StandardCharsets.UTF_8).length + 8
        ));
        buffer.putUtf(bundle);
        buffer.putVarInt(id);

        int metaSize = buffer.getRaw().position();

        BufferUtil packetData = BufferUtil.of(ByteBuffer.allocate(128));
        packet.write(packetData);
        packetData.trim().with(ByteBuffer::flip);

        buffer.ensure(packetData.getRaw().capacity()).getRaw()
                .put(packetData.getRaw());
        buffer.trim();

        int packetSize = buffer.map(ByteBuffer::position) - metaSize;
        if (packetSize > MAX_PACKET_SIZE) {
            throw new IllegalStateException("Packet %s exceeded max size! (Got: %s, Max: %s)".formatted(
                    packet.getClass().getSimpleName(), packetSize, MAX_PACKET_SIZE
            ));
        }

        return buffer.with(ByteBuffer::flip);
    }

}
