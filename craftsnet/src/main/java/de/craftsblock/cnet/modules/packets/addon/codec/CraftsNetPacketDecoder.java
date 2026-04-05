package de.craftsblock.cnet.modules.packets.addon.codec;

import de.craftsblock.cnet.modules.packets.common.packet.Packet;
import de.craftsblock.cnet.modules.packets.common.packet.codec.PacketDecoder;
import de.craftsblock.craftscore.buffer.BufferUtil;
import de.craftsblock.craftsnet.api.websocket.Frame;
import de.craftsblock.craftsnet.api.websocket.codec.WebSocketSafeTypeDecoder;

/**
 * A {@link WebSocketSafeTypeDecoder} implementation for {@link Packet} instances.
 * <p>
 * Uses an internal {@link PacketDecoder} to convert WebSocket {@link Frame}
 * data into {@link Packet} objects. This decoder allows packets to be
 * safely received and interpreted from the WebSocket network.
 *
 * @author CraftsBlock
 * @version 1.0.0
 * @since 1.0.0
 */
public final class CraftsNetPacketDecoder implements WebSocketSafeTypeDecoder<Packet> {

    private static final PacketDecoder PACKET_DECODER = new PacketDecoder();

    /**
     * Decodes a {@link Packet} from the given WebSocket {@link Frame}.
     * <p>
     * Delegates the decoding process to the internal {@link PacketDecoder},
     * using the frame's buffer as input.
     *
     * @param frame The WebSocket frame containing packet data.
     * @return The decoded {@link Packet}.
     */
    @Override
    public Packet decode(Frame frame) {
        return PACKET_DECODER.decode(BufferUtil.wrap(frame.getData()));
    }

}
