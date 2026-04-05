package de.craftsblock.cnet.modules.packets.common.networker.builtin;

import de.craftsblock.cnet.modules.packets.common.networker.Networker;
import de.craftsblock.cnet.modules.packets.common.networker.environment.Environment;
import de.craftsblock.cnet.modules.packets.common.packet.Packet;
import de.craftsblock.cnet.modules.packets.common.packet.codec.PacketEncoder;
import de.craftsblock.craftscore.utils.id.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.net.http.WebSocket;
import java.nio.ByteBuffer;

/**
 * Represents a network connection using a {@link WebSocket}.
 * <p>
 * This class implements the {@link Networker} interface and provides
 * methods to send packets, disconnect clients, and retrieve environment
 * and identifier information. Each instance is uniquely identified by
 * a {@link Snowflake}-generated ID.
 * </p>
 *
 * @param id          The id of this networker.
 * @param environment The environment containing the packet system context.
 * @param webSocket   The underlying WebSocket used for communication.
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 1.0.0
 * @see Networker
 * @see Packet
 * @see WebSocket
 * @since 1.0.0
 */
public record WebSocketNetworker(long id, Environment environment, WebSocket webSocket) implements Networker {

    /**
     * Constructs a new {@link WebSocketNetworker} with an automatically
     * generated unique ID.
     *
     * @param environment The environment containing the packet system context.
     * @param webSocket   The underlying WebSocket used for communication.
     */
    public WebSocketNetworker(Environment environment, WebSocket webSocket) {
        this(Snowflake.generate(), environment, webSocket);
    }

    /**
     * Sends a {@link Packet} to the client through the underlying {@link WebSocket}.
     * <p>
     * The packet is first encoded using {@link PacketEncoder} and then transmitted as a binary frame.
     *
     * @param packet The packet to send.
     */
    @Override
    public synchronized void send(@NotNull Packet packet) {
        PacketEncoder packetEncoder = new PacketEncoder(environment.getWebSocketPackets());
        ByteBuffer message = packetEncoder.encode(packet).getRaw();
        webSocket().sendBinary(message, true).join();
    }

    /**
     * Disconnects the client gracefully using close code {@code 1000} (normal closure)
     * and an empty reason.
     */
    @Override
    public synchronized void disconnect() {
        webSocket().sendClose(1000, "").join();
    }

    /**
     * Disconnects the client using the given close code and reason.
     *
     * @param code   The WebSocket close code (between 1000 and 4999).
     * @param reason The textual reason for the disconnection.
     */
    @Override
    public synchronized void disconnect(@Range(from = 1000, to = 4999) int code, @NotNull String reason) {
        webSocket().sendClose(code, reason).join();
    }

    /**
     * Returns the unique identifier of this networker instance.
     *
     * @return The unique ID.
     */
    @Override
    public synchronized long getId() {
        return id();
    }

    /**
     * Returns the {@link Environment} associated with this networker.
     *
     * @return The environment containing the
     * {@link de.craftsblock.cnet.modules.packets.common.WebSocketPackets WebSocketPackets} context.
     */
    @Override
    public Environment getEnvironment() {
        return environment();
    }

}
