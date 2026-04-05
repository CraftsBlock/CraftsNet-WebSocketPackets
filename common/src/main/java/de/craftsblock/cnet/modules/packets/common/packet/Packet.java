package de.craftsblock.cnet.modules.packets.common.packet;

import de.craftsblock.cnet.modules.packets.common.networker.Networker;
import de.craftsblock.cnet.modules.packets.common.packet.listener.PacketListener;
import de.craftsblock.craftscore.buffer.BufferUtil;

/**
 * Represents a network {@code Packet} that can be serialized, sent, and handled.
 * <p>
 * Implementations of this interface define how a packet is written to a
 * {@link BufferUtil} and how it should be processed when received by a
 * {@link Networker}. Packets may also retrieve their corresponding
 * {@link PacketListener} from the network environment to delegate handling logic.
 *
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 1.0.0
 * @see BufferUtil
 * @see Networker
 * @see PacketListener
 * @since 1.0.0
 */
public interface Packet extends BufferWritable {

    /**
     * Handles the packet logic when received by a {@link Networker}.
     *
     * @param networker The networker instance responsible for processing this packet.
     */
    void handle(Networker networker);

    /**
     * Retrieves a specific type of {@link PacketListener} from the network environment.
     *
     * @param networker The networker instance from which to obtain the listener registry.
     * @param type      The class type of the desired {@link PacketListener}.
     * @param <L>       The type of listener to retrieve.
     * @return The listener instance of the specified type, or {@code null} if not registered.
     */
    default <L extends PacketListener> L getPacketListener(Networker networker, Class<L> type) {
        return networker.getEnvironment().getPacketListenerRegistry().get(type);
    }

}
