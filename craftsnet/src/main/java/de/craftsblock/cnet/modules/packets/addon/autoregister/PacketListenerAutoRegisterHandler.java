package de.craftsblock.cnet.modules.packets.addon.autoregister;

import de.craftsblock.cnet.modules.packets.common.WebSocketPackets;
import de.craftsblock.cnet.modules.packets.common.packet.listener.PacketListener;
import de.craftsblock.cnet.modules.packets.common.packet.listener.PacketListenerRegistry;
import de.craftsblock.craftsnet.CraftsNet;
import de.craftsblock.craftsnet.autoregister.AutoRegisterHandler;
import de.craftsblock.craftsnet.autoregister.meta.AutoRegisterInfo;

/**
 * A handler for automatically registering {@link PacketListener} implementations. This class extends
 * {@link AutoRegisterHandler} and provides a concrete implementation for handling the registration of
 * {@link PacketListener} instances into the packet listener registry of {@link WebSocketPackets}.
 *
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 1.0.0
 * @since 1.0.1
 */
public class PacketListenerAutoRegisterHandler extends AutoRegisterHandler<PacketListener> {

    private final PacketListenerRegistry registry;

    /**
     * Constructs an {@link PacketListenerAutoRegisterHandler} with the specified {@link CraftsNet} and {@link WebSocketPackets} instance.
     *
     * @param craftsNet        The main {@link CraftsNet} instance.
     * @param webSocketPackets The {@link WebSocketPackets} instance, which holds the {@link PacketListenerRegistry}.
     */
    public PacketListenerAutoRegisterHandler(CraftsNet craftsNet, WebSocketPackets webSocketPackets) {
        super(craftsNet);
        this.registry = webSocketPackets.getPacketListenerRegistry();
    }

    /**
     * Handles the registration of the provided {@link PacketListener}.
     *
     * <p>This method attempts to register the given {@link PacketListener} with the
     * {@link WebSocketPackets#getPacketListenerRegistry()} of the associated
     * {@link WebSocketPackets} instance. If registration is successful, the method
     * returns {@code true}.</p>
     *
     * @param packetListener The {@link PacketListener} to be registered.
     * @param args           Additional arguments (not used in this implementation but provided for extensibility).
     * @return {@code true} if the registration was successful, {@code false} otherwise.
     */
    @Override
    protected boolean handle(PacketListener packetListener, AutoRegisterInfo info, Object... args) {
        if (registry.isRegistered(packetListener)) {
            return false;
        }

        registry.register(packetListener);
        return true;
    }

}