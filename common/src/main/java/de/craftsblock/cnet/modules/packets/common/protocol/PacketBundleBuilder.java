package de.craftsblock.cnet.modules.packets.common.protocol;

import de.craftsblock.cnet.modules.packets.common.packet.Packet;
import de.craftsblock.craftscore.buffer.BufferUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

/**
 * A builder class for constructing immutable {@link PacketBundle} instances.
 * <p>
 * This builder allows packet classes to be registered with associated deserializer
 * functions. Each packet is automatically assigned a unique numeric ID in the
 * order of registration. Once built, the {@link PacketBundle} provides a
 * versioned, immutable collection of these packet mappings and their generators.
 *
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 1.0.0
 * @see PacketBundle
 * @since 1.0.0
 */
public class PacketBundleBuilder {

    private final @NotNull String identifier;
    private final @Range(from = 0, to = Integer.MAX_VALUE) int version;

    private final @NotNull HashMap<Class<? extends Packet>, Integer> packetIDs = new HashMap<>();
    private final @NotNull List<Function<BufferUtil, ? extends Packet>> deserializers = new ArrayList<>();

    /**
     * Creates a new {@code PacketBundleBuilder} for the given identifier and version.
     *
     * @param identifier The identifier of the bundle to be built.
     * @param version    The version number of the bundle (non-negative).
     */
    PacketBundleBuilder(@NotNull String identifier,
                        @Range(from = 0, to = Integer.MAX_VALUE) int version) {
        this.identifier = identifier;
        this.version = version;
    }

    /**
     * Registers a new packet class with its deserializer function.
     * <p>
     * Packets are assigned an ID in the order they are added, starting at 0.
     * If the packet class has already been registered, this method throws
     * an exception to prevent accidental duplicate registrations.
     *
     * @param packetClass The packet class to register.
     * @param generator   The deserializer function that reconstructs the packet from a {@link BufferUtil}.
     * @param <P>         The type of packet being registered.
     * @return This builder instance for method chaining.
     * @throws IllegalStateException If the packet class has already been registered.
     */
    public synchronized <P extends Packet> PacketBundleBuilder addPacket(@NotNull Class<P> packetClass,
                                                                         @NotNull Function<BufferUtil, P> generator) {
        int packetID = deserializers.size();
        Integer formerPacketID = packetIDs.put(packetClass, packetID);

        if (formerPacketID != null)
            throw new IllegalStateException("The packet %s was already registered to id %s!".formatted(
                    packetClass.getName(), formerPacketID
            ));

        deserializers.add(generator);
        return this;
    }

    /**
     * Builds a new immutable {@link PacketBundle} from the registered packets and metadata.
     *
     * @return A fully constructed {@link PacketBundle}.
     */
    public synchronized @NotNull PacketBundle build() {
        return new PacketBundle(
                this.identifier,
                this.version,
                Collections.unmodifiableMap(packetIDs),
                Collections.unmodifiableList(deserializers)
        );
    }

}
