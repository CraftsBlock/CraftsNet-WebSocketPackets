package de.craftsblock.cnet.modules.packets.common.packet.entities;

import de.craftsblock.cnet.modules.packets.common.packet.BufferWritable;
import de.craftsblock.cnet.modules.packets.common.packet.Packet;
import de.craftsblock.craftscore.buffer.BufferUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a {@link Packet} that wraps an {@link Entity} instance.
 * <p>
 * This interface provides default serialization logic by delegating
 * the {@link #write(BufferUtil)} method to the wrapped entity.
 * Entities are uniquely identified and can be sent over the network as packets.
 *
 * @param <E> The type of {@link BufferWritable} wrapped by this packet.
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 1.0.0
 * @see Packet
 * @see BufferWritable
 * @since 1.0.0
 */
public interface EntityPacket<E extends Entity> extends Packet {

    /**
     * Serializes the wrapped entity into the provided buffer.
     *
     * @param buffer The buffer to write the entity data into.
     */
    @Override
    default void write(@NotNull BufferUtil buffer) {
        getEntity().write(buffer);
    }

    /**
     * Retrieves the entity instance wrapped by this packet.
     *
     * @return The entity instance.
     */
    E getEntity();

}
