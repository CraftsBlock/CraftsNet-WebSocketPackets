package de.craftsblock.cnet.modules.packets.common.packet;

import de.craftsblock.craftscore.buffer.BufferUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a data structure that can be serialized into a {@link BufferUtil}.
 * <p>
 * Implementing classes define how their internal data is written to the buffer for
 * network transmission. It is commonly used in combination with {@link Packet}
 * implementations to separate serialization from processing behavior.
 *
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 1.0.0
 * @see BufferUtil
 * @since 1.0.0
 */
public interface BufferWritable {

    /**
     * Serializes the implementing object into the provided {@link BufferUtil}.
     *
     * @param buffer The buffer to write the object's data into.
     */
    void write(@NotNull BufferUtil buffer);

}
