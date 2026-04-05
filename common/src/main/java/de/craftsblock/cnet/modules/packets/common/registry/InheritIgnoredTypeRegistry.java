package de.craftsblock.cnet.modules.packets.common.registry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A {@link Registry} implementation that uses a thread-safe
 * {@link ConcurrentHashMap} to store entries.
 * <p>
 * The key characteristic of this registry is that it ignores inheritance:
 * only the exact type registered as a key can be used for lookups.
 * Subtypes and supertypes are not considered equivalent. For example,
 * registering a subclass will not make it retrievable via its superclass.
 *
 * @param <T> The type of objects stored in this registry
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 1.0.0
 * @see Registry
 * @since 1.0.0
 */
public class InheritIgnoredTypeRegistry<T> implements Registry<T> {

    private final Map<Class<? extends T>, T> storage = new ConcurrentHashMap<>();

    /**
     * Registers a value for the given key type.
     *
     * @param key   The type key under which to register the value.
     * @param value The value to register.
     * @throws IllegalStateException If there is already a value for the key.
     */
    @Override
    public void register(Class<? extends T> key, T value) {
        if (isRegistered(key)) {
            throw new IllegalStateException("There is already a value for key %s!".formatted(key.getName()));
        }

        storage.put(key, value);
    }

    /**
     * Unregisters the value associated with the given key type.
     *
     * @param key The type key to remove.
     * @return The previously registered value, or {@code null} if none existed.
     */
    @Override
    public T unregister(Class<? extends T> key) {
        return storage.remove(key);
    }

    /**
     * Retrieves the value registered for the given key type, or returns
     * a fallback if no value is found. Only exact type matches are
     * considered, inheritance is ignored.
     *
     * @param key      The type key to look up.
     * @param fallback The fallback value to return if no value is registered.
     * @param <R>      The expected subtype of {@code T}.
     * @return The registered value, or the fallback if none found.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <R extends T> R getOrDefault(@NotNull Class<? extends R> key, @Nullable R fallback) {
        if (storage.containsKey(key)) {
            return (R) storage.get(key);
        }

        return fallback;
    }

    /**
     * Checks whether a value has been registered for the given key type.
     * <p>
     * Only exact type matches are considered, inheritance is ignored.
     *
     * @param type The type key to check, may be {@code null}.
     * @return {@code true} if a value is registered for the key, {@code false} otherwise.
     */
    @Override
    public boolean isRegistered(@Nullable Class<? extends T> type) {
        if (type == null) {
            return false;
        }

        return storage.containsKey(type);
    }

}
