package de.craftsblock.cnet.modules.packets.common.networker;

import de.craftsblock.cnet.modules.packets.common.networker.environment.Environment;
import de.craftsblock.cnet.modules.packets.common.packet.Packet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * Represents a network connection capable of sending {@link Packet} instances
 * and managing connection lifecycle events such as disconnection.
 * <p>
 * Implementations typically wrap an underlying transport mechanism such as a
 * WebSocket or TCP socket. Each networker has a unique identifier and is
 * associated with an {@link Environment} providing contextual data.
 * </p>
 *
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 1.0.0
 * @since 1.0.0
 */
public interface Networker {

    /**
     * Sends a {@link Packet} to this network connection.
     *
     * @param packet The packet to send, never {@code null}
     */
    void send(@NotNull Packet packet);

    /**
     * Disconnects the network connection immediately,
     * using a default closure code and no explicit reason.
     */
    void disconnect();

    /**
     * Disconnects the network connection with the given textual reason,
     * using the 1000 (Normal) closure code by default.
     *
     * @param reason The reason for disconnecting, never {@code null}
     */
    default void disconnect(@NotNull String reason) {
        disconnect(1000, reason);
    }

    /**
     * Disconnects the network connection with the specified closure code and reason.
     *
     * @param code   The numeric closure code to send, in range 1000–4999
     * @param reason The reason for disconnecting, never {@code null}
     */
    void disconnect(@Range(from = 1000, to = 4999) int code, @NotNull String reason);

    /**
     * Returns the unique identifier of this network connection.
     *
     * @return A unique connection ID
     */
    long getId();

    /**
     * Returns the {@link Environment} associated with this networker.
     *
     * @return The environment instance.
     */
    Environment getEnvironment();

}
