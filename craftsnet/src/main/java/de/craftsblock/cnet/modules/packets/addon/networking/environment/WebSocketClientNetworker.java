package de.craftsblock.cnet.modules.packets.addon.networking.environment;

import de.craftsblock.cnet.modules.packets.common.networker.Networker;
import de.craftsblock.cnet.modules.packets.common.networker.environment.Environment;
import de.craftsblock.cnet.modules.packets.common.packet.Packet;
import de.craftsblock.craftscore.utils.id.Snowflake;
import de.craftsblock.craftsnet.api.websocket.WebSocketClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * A {@link Networker} implementation backed by a {@link WebSocketClient}.
 * <p>
 * Provides methods to send {@link Packet packets} to the client, disconnect the client,
 * and retrieve the unique identifier and associated {@link Environment}.
 * Each instance is uniquely identified by a {@link Snowflake}-generated ID.
 *
 * @param id          The unique identifier for this networker instance.
 * @param environment The environment associated with this networker, never {@code null}.
 * @param client      The WebSocketClient used for communication, never {@code null}.
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 1.0.0
 * @since 1.0.0
 */
public record WebSocketClientNetworker(long id, Environment environment, WebSocketClient client) implements Networker {

    /**
     * Constructs a new {@link WebSocketClientNetworker} with an automatically generated unique ID.
     *
     * @param environment The environment associated with this networker, never {@code null}.
     * @param client The WebSocketClient used for communication, never {@code null}.
     * @throws IllegalStateException If the environment is null.
     */
    public WebSocketClientNetworker(Environment environment, WebSocketClient client) {
        this(Snowflake.generate(), environment, client);

        if (environment == null) {
            throw new IllegalStateException("Must be located inside a valid environment! (Got: null)");
        }
    }

    /**
     * Sends a {@link Packet} to the client through the underlying {@link WebSocketClient}.
     *
     * @param packet The packet to send.
     */
    @Override
    public void send(@NotNull Packet packet) {
        client().sendMessage(packet);
    }

    /**
     * Disconnects the client gracefully without a specific close code or reason.
     */
    @Override
    public void disconnect() {
        client().close();
    }

    /**
     * Disconnects the client using the given close code and reason.
     *
     * @param code   The WebSocket close code (between 1000 and 4999).
     * @param reason The textual reason for the disconnection.
     */
    @Override
    public void disconnect(@Range(from = 1000, to = 4999) int code, @NotNull String reason) {
        client.close(code, reason);
    }

    /**
     * Returns the unique identifier of this networker instance.
     *
     * @return The unique ID.
     */
    @Override
    public long getId() {
        return id();
    }

    /**
     * Returns the {@link Environment} associated with this networker.
     *
     * @return The environment, never {@code null}.
     */
    @Override
    public Environment getEnvironment() {
        return environment();
    }

}
