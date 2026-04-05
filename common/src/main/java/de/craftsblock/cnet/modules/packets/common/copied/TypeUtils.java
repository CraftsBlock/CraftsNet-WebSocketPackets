package de.craftsblock.cnet.modules.packets.common.copied;

import org.jetbrains.annotations.Contract;

import java.lang.reflect.*;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

/* Copied from the craftsnet project must be removed when migrating the reflections
 * to craftscore!
 */

/**
 * Utility class for handling Java primitive types and their wrapper classes.
 * Provides methods to check type assignability, convert between primitive and wrapper types,
 * and compare types for equivalence considering primitive-wrapper relationships.
 * <p>
 * This class is designed to assist reflection operations where understanding
 * the relationship between primitive types and their wrappers is essential.
 * </p>
 *
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 1.0.3
 * @since 3.5.0
 */
public class TypeUtils {

    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER = buildWrapperMap();

    private static Map<Class<?>, Class<?>> buildWrapperMap() {
        Map<Class<?>, Class<?>> wrappers = new IdentityHashMap<>(9);
        wrappers.put(Boolean.TYPE, Boolean.class);
        wrappers.put(Byte.TYPE, Byte.class);
        wrappers.put(Character.TYPE, Character.class);
        wrappers.put(Short.TYPE, Short.class);
        wrappers.put(Integer.TYPE, Integer.class);
        wrappers.put(Long.TYPE, Long.class);
        wrappers.put(Float.TYPE, Float.class);
        wrappers.put(Double.TYPE, Double.class);
        wrappers.put(Void.TYPE, Void.class);
        return Collections.unmodifiableMap(wrappers);
    }

    /**
     * Private constructor to prevent direct instantiation
     */
    private TypeUtils() {
    }

    /**
     * Determines if the {@code sourceType} can be assigned to the {@code targetType},
     * considering both primitive types and their corresponding wrapper classes.
     *
     * <p>
     * This method returns {@code true} if:
     * <ul>
     *     <li>{@code targetType} is assignable from {@code sourceType}</li>
     *     <li>{@code targetType} is a primitive and {@code sourceType} is its wrapper type</li>
     *     <li>{@code sourceType} is a primitive and {@code targetType} is its wrapper type</li>
     * </ul>
     * Otherwise, it returns {@code false}.
     *
     * @param targetType The target type to assign to; may be primitive or wrapper.
     * @param sourceType The source type to assign from; may be primitive or wrapper.
     * @return {@code true} if the {@code sourceType} is assignable to {@code targetType}, {@code false} otherwise.
     */
    @Contract(value = "null, null -> false", pure = true)
    public static boolean isAssignable(Class<?> targetType, Class<?> sourceType) {
        if (targetType == null || sourceType == null) return false;
        if (targetType.isAssignableFrom(sourceType)) return true;

        // Unpack array types
        if (targetType.isArray() && sourceType.isArray())
            return isAssignable(targetType.componentType(), sourceType.componentType());

        if (isPrimitive(targetType)) {
            var wrapper = toWrapper(targetType);
            return wrapper.equals(sourceType);
        }

        if (isPrimitive(sourceType)) {
            var wrapper = toWrapper(sourceType);
            return targetType.isAssignableFrom(wrapper);
        }

        return false;
    }

    /**
     * Converts the given {@code type} to its corresponding wrapper class if it is a primitive type.
     * If the given type is not primitive or is {@code null}, it is returned unchanged.
     *
     * @param type The type to convert; may be primitive or wrapper.
     * @return The wrapper class if {@code type} is primitive; otherwise, returns {@code type} unchanged.
     */
    @Contract(value = "null -> null; !null -> !null", pure = true)
    public static Class<?> toWrapper(Class<?> type) {
        return type != null && type.isPrimitive()
                ? PRIMITIVE_TO_WRAPPER.get(type)
                : type;
    }

    /**
     * Checks if the given {@code type} represents a primitive type.
     *
     * @param type The type to check.
     * @return {@code true} if {@code type} is a primitive type, {@code false} otherwise.
     */
    @Contract(value = "null -> false", pure = true)
    public static boolean isPrimitive(Class<?> type) {
        return type != null && type.isPrimitive();
    }

}
