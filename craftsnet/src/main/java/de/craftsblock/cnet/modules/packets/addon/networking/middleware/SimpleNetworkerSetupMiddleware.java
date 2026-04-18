package de.craftsblock.cnet.modules.packets.addon.networking.middleware;

import de.craftsblock.cnet.modules.packets.addon.WebSocketPacketsAddon;
import de.craftsblock.cnet.modules.packets.addon.networking.environment.WebSocketClientNetworker;
import de.craftsblock.craftsnet.CraftsNet;
import de.craftsblock.craftsnet.api.middlewares.MiddlewareCallbackInfo;
import de.craftsblock.craftsnet.api.middlewares.WebsocketMiddleware;
import de.craftsblock.craftsnet.api.websocket.SocketExchange;
import de.craftsblock.craftsnet.api.websocket.WebSocketClient;

/**
 * A middleware that automatically sets up and tears down
 * {@link WebSocketClientNetworker} instances for {@link WebSocketClient}s.
 * <p>
 * On connection, a {@link WebSocketClientNetworker} is created and stored in
 * the client's context. On disconnection, the entry is removed.
 * <p>
 * This allows simple retrieval of the associated networker instance
 * from any {@link WebSocketClient}.
 *
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 1.0.0
 * @see WebsocketMiddleware
 * @see WebSocketClientNetworker
 * @since 1.0.0
 */
public class SimpleNetworkerSetupMiddleware implements WebsocketMiddleware {

    /**
     * Called when a new WebSocket connection is established.
     * <p>
     * Creates a {@link WebSocketClientNetworker} and stores it in the
     * session of the connecting client.
     *
     * @param callbackInfo The middleware callback context.
     * @param exchange     The WebSocket exchange containing connection details.
     */
    @Override
    public void handleConnect(MiddlewareCallbackInfo callbackInfo, SocketExchange exchange) {
        CraftsNet craftsNet = exchange.server().getCraftsNet();
        WebSocketPacketsAddon addon = craftsNet.getAddonManager().getAddon(WebSocketPacketsAddon.class);

        if (addon == null)
            throw new IllegalStateException("The websocket packets addon is not loaded!");

        WebSocketClient client = exchange.client();
        client.getContext().put(new WebSocketClientNetworker(addon.getEnvironment(), client));
    }

    /**
     * Called when a WebSocket connection is closed.
     * <p>
     * Removes the {@link WebSocketClientNetworker} from the client session.
     *
     * @param callbackInfo The middleware callback context.
     * @param exchange     The WebSocket exchange containing connection details.
     */
    @Override
    public void handleDisconnect(MiddlewareCallbackInfo callbackInfo, SocketExchange exchange) {
        exchange.client().getContext().remove(WebSocketClientNetworker.class);
    }

    /**
     * Retrieves the {@link WebSocketClientNetworker} associated with a given
     * {@link WebSocketClient}, or {@code null} if none exists.
     *
     * @param client The WebSocket client.
     * @return The associated {@link WebSocketClientNetworker}, or {@code null} if not set.
     */
    public static WebSocketClientNetworker getNetworker(WebSocketClient client) {
        return client.getContext().getTyped(WebSocketClientNetworker.class);
    }

}
