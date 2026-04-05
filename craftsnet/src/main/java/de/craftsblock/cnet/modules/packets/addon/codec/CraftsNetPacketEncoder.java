package de.craftsblock.cnet.modules.packets.addon.codec;

import de.craftsblock.cnet.modules.packets.common.packet.Packet;
import de.craftsblock.cnet.modules.packets.common.packet.codec.PacketEncoder;
import de.craftsblock.craftscore.buffer.BufferUtil;
import de.craftsblock.craftsnet.api.websocket.codec.WebSocketSafeTypeEncoder;

/**
 * A {@link WebSocketSafeTypeEncoder.TypeToBufferUtilEncoder} implementation for {@link Packet} instances.
 * <p>
 * Wraps a {@link PacketEncoder} to convert packets into {@link BufferUtil} instances
 * suitable for safe transmission over WebSocket connections.
 *
 * @param encoder The underlying {@link PacketEncoder} used for encoding
 *                {@link Packet} instances, never {@code null}.
 * @author CraftsBlock
 * @version 1.0.0
 * @since 1.1.0
 */
public record CraftsNetPacketEncoder(PacketEncoder encoder) implements WebSocketSafeTypeEncoder.TypeToBufferUtilEncoder<Packet> {

    /**
     * Encodes a {@link Packet} into a {@link BufferUtil} using the underlying {@link PacketEncoder}.
     *
     * @param packet The packet to encode.
     * @return A ByteBuffer containing the encoded packet data.
     */
    @Override
    public BufferUtil encode(Packet packet) {
        return encoder.encode(packet);
    }

}
