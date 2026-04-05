package de.craftsblock.cnet.modules.packets.common;

import de.craftsblock.cnet.modules.packets.common.packet.listener.PacketListenerRegistry;
import de.craftsblock.cnet.modules.packets.common.protocol.PacketBundleRegistry;
import org.jetbrains.annotations.NotNull;

/**
 * Central manager for the WebSocket packet system.
 * <p>
 * This class follows a singleton pattern and is responsible for initializing
 * and providing access to packet listeners and packet bundle registries.
 * It ensures that only one instance exists per class loader.
 * </p>
 * <p>
 * Usage typically involves calling {@link #onLoad()} to initialize the system,
 * accessing registries via {@link #getPacketListenerRegistry()} and
 * {@link #getPacketBundleRegistry()}, and calling {@link #onDisable()} when
 * shutting down.
 * </p>
 *
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 1.0.0
 * @since 1.0.0
 */
public class WebSocketPackets {

    private static WebSocketPackets instance;

    private PacketListenerRegistry packetListenerRegistry;
    private PacketBundleRegistry packetBundleRegistry;

    /**
     * Initializes the {@link WebSocketPackets} system.
     * <p>
     * This method sets up the singleton instance and initializes the
     * {@link PacketListenerRegistry} and {@link PacketBundleRegistry}.
     * Calling this method more than once will throw an exception.
     * </p>
     *
     * @throws IllegalStateException if the system is loaded more than once.
     */
    public void onLoad() {
        if (instance != null) {
            throw new IllegalStateException("%s can not be loaded twice by the same class loader!".formatted(
                    this.getClass().getSimpleName()
            ));
        }

        instance = this;

        this.packetListenerRegistry = new PacketListenerRegistry();
        this.packetBundleRegistry = new PacketBundleRegistry();
    }

    /**
     * Disables the {@link WebSocketPackets} system.
     * <p>
     * Clears the singleton instance. Should be called during shutdown.
     * </p>
     */
    public void onDisable() {
        instance = null;
    }

    /**
     * Returns the registry responsible for packet listeners.
     *
     * @return the {@link PacketListenerRegistry} instance
     */
    public PacketListenerRegistry getPacketListenerRegistry() {
        return packetListenerRegistry;
    }

    /**
     * Returns the registry responsible for packet bundles.
     *
     * @return the {@link PacketBundleRegistry} instance
     */
    public PacketBundleRegistry getPacketBundleRegistry() {
        return packetBundleRegistry;
    }

    /**
     * Returns the current singleton instance of {@link WebSocketPackets}.
     *
     * @return the current {@link WebSocketPackets} instance, or {@code null} if not loaded
     */
    public static WebSocketPackets getInstance() {
        return instance;
    }

    /**
     * Returns the current singleton instance of {@link WebSocketPackets} safely.
     *
     * @return the current {@link WebSocketPackets} instance
     * @throws IllegalStateException if the system is not loaded
     */
    public static @NotNull WebSocketPackets getInstanceSafely() {
        loadedOrThrow();
        return instance;
    }
    /**
     * Checks if the {@link WebSocketPackets} system is loaded.
     *
     * @throws IllegalStateException if the system is not loaded
     */
    public static void loadedOrThrow() {
        WebSocketPackets webSocketPackets = WebSocketPackets.getInstance();
        if (webSocketPackets != null) {
            return;
        }

        throw new IllegalStateException("%s must be loaded!".formatted(WebSocketPackets.class.getSimpleName()));
    }

}
