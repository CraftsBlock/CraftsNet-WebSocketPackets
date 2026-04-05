package de.craftsblock.cnet.modules.packets.common.packet.listener;

import de.craftsblock.cnet.modules.packets.common.copied.TypeUtils;
import de.craftsblock.cnet.modules.packets.common.registry.InheritIgnoredTypeRegistry;

import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A registry for managing {@link PacketListener} instances.
 * <p>
 * This registry extends {@link InheritIgnoredTypeRegistry} and ensures that
 * packet listeners are properly registered and unregistered throughout
 * the inheritance hierarchy. When a listener is registered for a subclass,
 * it will also propagate to all parent classes and interfaces up to
 * {@link PacketListener}, maintaining type-aware listener mappings.
 *
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 1.0.0
 * @see PacketListener
 * @see InheritIgnoredTypeRegistry
 * @since 1.0.0
 */
public class PacketListenerRegistry extends InheritIgnoredTypeRegistry<PacketListener> {

    private static final Class<PacketListener> TYPE_ROOT = PacketListener.class;

    /**
     * Registers a {@link PacketListener} for a specific listener class.
     *
     * @param key   The class of the packet listener to register.
     * @param value The listener instance to associate with the class.
     */
    @Override
    public void register(Class<? extends PacketListener> key, PacketListener value) {
        Stack<Class<?>> walked = new Stack<>();

        try {
            this.register0(key, value, walked);
        } finally {
            walked.clear();
        }
    }

    /**
     * Helper method that performs recursive registration along the inheritance tree.
     *
     * @param key    The listener class to register.
     * @param value  The listener instance.
     * @param walked The stack used to prevent cyclic registration loops.
     */
    private void register0(Class<? extends PacketListener> key, PacketListener value, Stack<Class<?>> walked) {
        if (walked.contains(key) || key == null
                || Object.class.equals(key) || TYPE_ROOT.equals(key)) {
            return;
        }

        walked.push(key);

        super.register(key, value);
        this.performInInheritTree(key, value, (a, b) -> this.register0(a, b, walked));
    }

    /**
     * Unregisters the {@link PacketListener} associated with a specific listener class.
     *
     * @param key The class of the packet listener to unregister.
     * @return The unregistered listener instance, or {@code null} if none was registered.
     */
    @Override
    public PacketListener unregister(Class<? extends PacketListener> key) {
        Stack<Class<?>> walked = new Stack<>();

        try {
            return this.unregister0(key, walked);
        } finally {
            walked.clear();
        }
    }

    /**
     * Helper method that performs recursive unregistration along the inheritance tree.
     *
     * @param key    The listener class to unregister.
     * @param walked The stack used to prevent cyclic unregistration loops.
     * @return The unregistered listener instance, or {@code null} if none was registered.
     */
    private PacketListener unregister0(Class<? extends PacketListener> key, Stack<Class<?>> walked) {
        if (walked.contains(key)) {
            return null;
        }

        walked.push(key);

        this.performInInheritTree(key, a -> this.unregister0(a, walked));
        return super.unregister(key);
    }

    /**
     * Traverses the inheritance tree of a listener class and performs the given action
     * on each parent class or interface that extends {@link PacketListener}.
     *
     * @param key      The listener class whose inheritance tree is traversed.
     * @param consumer The action to perform on each applicable parent class or interface.
     */
    private void performInInheritTree(Class<? extends PacketListener> key,
                                      Consumer<Class<? extends PacketListener>> consumer) {
        performInInheritTree(key, null, (a, b) -> consumer.accept(a));
    }

    /**
     * Traverses the inheritance tree of a listener class and performs the given action
     * on each parent class or interface that extends {@link PacketListener}, passing the
     * associated listener instance if provided.
     *
     * @param key      The listener class whose inheritance tree is traversed.
     * @param value    The listener instance associated with the class, can be {@code null}.
     * @param consumer The action to perform on each applicable parent class or interface.
     */
    @SuppressWarnings("unchecked")
    private void performInInheritTree(Class<? extends PacketListener> key,
                                      PacketListener value,
                                      BiConsumer<Class<? extends PacketListener>, PacketListener> consumer) {
        Class<?> superclass = key.getSuperclass();
        if (TypeUtils.isAssignable(TYPE_ROOT, superclass)) {
            consumer.accept((Class<? extends PacketListener>) superclass, value);
        }

        for (Class<?> face : key.getInterfaces()) {
            if (TypeUtils.isAssignable(TYPE_ROOT, face)) {
                consumer.accept((Class<? extends PacketListener>) face, value);
            }
        }
    }

}
