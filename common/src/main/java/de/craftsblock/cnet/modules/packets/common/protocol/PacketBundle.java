package de.craftsblock.cnet.modules.packets.common.protocol;

import de.craftsblock.cnet.modules.packets.common.packet.Packet;
import de.craftsblock.craftscore.buffer.BufferUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Represents a packet bundle that encapsulates packet definitions,
 * deserializers, and metadata such as an identifier and version.
 * <p>
 * A {@code PacketBundle} acts as a container that maps packet classes
 * to numeric IDs and provides the ability to reconstruct packets
 * from raw {@link BufferUtil} data via registered deserializer functions.
 * It enables consistent packet encoding and decoding across instances
 * while ensuring versioned compatibility.
 *
 * @param identifier    A unique identifier for the packet bundle.
 * @param version       The version number of the packet bundle.
 * @param packetIDs     A mapping of packet classes to their associated numeric IDs.
 * @param deserializers A list of deserializer functions used to create packets from raw data by ID.
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 1.0.0
 * @since 1.0.0
 */
public record PacketBundle(@NotNull String identifier,
                           @Range(from = 0, to = Integer.MAX_VALUE) int version,
                           @Unmodifiable Map<Class<? extends Packet>, Integer> packetIDs,
                           @Unmodifiable List<Function<BufferUtil, ? extends Packet>> deserializers) {

    /**
     * Constructs a new {@code PacketBundle} with the provided identifier, version,
     * packet mappings, and deserializer functions. The identifier will always
     * be normalized to lowercase and trimmed of surrounding whitespace.
     *
     * @param identifier    The unique identifier of the bundle.
     * @param version       The version of the packet bundle.
     * @param packetIDs     A mapping of packet classes to their IDs.
     * @param deserializers A list of deserializer functions ordered by packet ID.
     * @throws IllegalArgumentException If the length of {@code packetIDs} and {@code deserializers} does not match.
     */
    public PacketBundle(@NotNull String identifier,
                        @Range(from = 0, to = Integer.MAX_VALUE) int version,
                        @Unmodifiable Map<Class<? extends Packet>, Integer> packetIDs,
                        @Unmodifiable List<Function<BufferUtil, ? extends Packet>> deserializers) {
        this.identifier = identifier.toLowerCase().trim();
        this.version = version;

        if (packetIDs.size() != deserializers.size()) {
            throw new IllegalArgumentException("The length of packets and deserializers must match! (Packets: %s; Deserializers: %s)".formatted(
                    packetIDs.size(), deserializers.size()
            ));
        }

        this.packetIDs = packetIDs;
        this.deserializers = deserializers;
    }

    /**
     * Creates a new {@link Packet} instance from the given packet ID and raw buffer data.
     *
     * @param id     The numeric ID of the packet to create.
     * @param buffer The buffer containing the serialized packet data.
     * @return A new {@link Packet} instance, or {@code null} if no deserializer exists for the given ID.
     */
    public Packet createPacket(@Range(from = 0, to = Integer.MAX_VALUE) int id,
                               @NotNull BufferUtil buffer) {
        Function<BufferUtil, ? extends Packet> generator = deserializers.get(id);
        if (generator == null) {
            return null;
        }

        return generator.apply(buffer);
    }

    /**
     * Retrieves the numeric ID associated with the given packet instance.
     *
     * @param packet The packet instance to look up.
     * @return The associated packet ID, or {@code -1} if the packet type is not registered.
     */
    public @Range(from = -1, to = Integer.MAX_VALUE) int getId(@Nullable Packet packet) {
        if (packet == null) {
            return -1;
        }

        return this.getId(packet.getClass());
    }

    /**
     * Retrieves the numeric ID associated with the given packet class.
     *
     * @param packetClass The packet class to look up.
     * @return The associated packet ID, or {@code -1} if the class is not registered.
     */
    public @Range(from = -1, to = Integer.MAX_VALUE) int getId(@Nullable Class<? extends Packet> packetClass) {
        if (packetClass == null) {
            return -1;
        }

        return packetIDs.getOrDefault(packetClass, -1);
    }

    /**
     * Checks whether the given packet instance is registered in this bundle.
     *
     * @param packet The packet instance to check.
     * @return {@code true} if the packet is registered, {@code false} otherwise.
     */
    public boolean containsPacket(@Nullable Packet packet) {
        if (packet == null) {
            return false;
        }

        return this.containsPacket(packet.getClass());
    }

    /**
     * Checks whether the given packet class is registered in this bundle.
     *
     * @param packetClass The packet class to check.
     * @return {@code true} if the class is registered, {@code false} otherwise.
     */
    public boolean containsPacket(@Nullable Class<? extends Packet> packetClass) {
        if (packetClass == null) {
            return false;
        }

        return packetIDs.containsKey(packetClass);
    }

    /**
     * Creates a new {@link PacketBundleBuilder} for the given identifier and version.
     * This provides a fluent API for constructing a {@code PacketBundle}.
     *
     * @param identifier The identifier for the packet bundle.
     * @param version    The version of the packet bundle.
     * @return A new {@link PacketBundleBuilder} instance.
     */
    public static PacketBundleBuilder create(@NotNull String identifier,
                                             @Range(from = 0, to = Integer.MAX_VALUE) int version) {
        return new PacketBundleBuilder(identifier, version);
    }

}
