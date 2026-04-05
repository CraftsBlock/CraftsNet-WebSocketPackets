package de.craftsblock.cnet.modules.packets.common.registry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A generic {@code Registry} abstraction for storing and managing objects
 * of type {@code T}, indexed by their concrete {@link Class}.
 *
 * @param <T> The type of objects to store in this registry
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 1.0.0
 * @since 1.0.0
 */
public interface Registry<T> {

    /**
     * Registers the given instance using its class as the key.
     *
     * @param t The instance to register, never {@code null}
     */
    @SuppressWarnings("unchecked")
    default void register(T t) {
        register((Class<? extends T>) t.getClass(), t);
    }

    /**
     * Registers the given instance with an explicit key type.
     *
     * @param key   The key class under which to register the instance
     * @param value The instance to register
     */
    void register(Class<? extends T> key, T value);

    /**
     * Unregisters the given instance, using its class as the key.
     *
     * @param t The instance to unregister
     * @return The unregistered instance, or {@code null} if none was found
     */
    @SuppressWarnings("unchecked")
    default T unregister(T t) {
        return this.unregister((Class<? extends T>) t.getClass());
    }

    /**
     * Unregisters the object bound to the given type.
     *
     * @param key The key class to unregister
     * @return The unregistered instance, or {@code null} if none was found
     */
    T unregister(Class<? extends T> key);

    /**
     * Retrieves the object registered under the given type.
     *
     * @param key The key class
     * @param <R> The expected subtype of {@code T}
     * @return The registered instance, or {@code null} if not found
     */
    default <R extends T> R get(@NotNull Class<? extends R> key) {
        return getOrDefault(key, null);
    }

    /**
     * Retrieves the object registered under the given type,
     * or returns a fallback if none is found.
     *
     * @param key      The key class
     * @param fallback The fallback value, may be {@code null}
     * @param <R>      The expected subtype of {@code T}
     * @return The registered instance, or the fallback if not found
     */
    <R extends T> R getOrDefault(@NotNull Class<? extends R> key, @Nullable R fallback);

    /**
     * Checks whether the given instance is registered in this registry,
     * using its class as the lookup key.
     *
     * @param t The instance to check, may be {@code null}
     * @return {@code true} if registered, {@code false} otherwise
     */
    @SuppressWarnings("unchecked")
    default boolean isRegistered(@Nullable T t) {
        if (t == null) {
            return false;
        }

        return this.isRegistered((Class<? extends T>) t.getClass());
    }

    /**
     * Checks whether the given type has been registered in this registry.
     *
     * @param type The type to check, may be {@code null}
     * @return {@code true} if an instance is registered for this type,
     * {@code false} otherwise
     */
    boolean isRegistered(@Nullable Class<? extends T> type);

}
