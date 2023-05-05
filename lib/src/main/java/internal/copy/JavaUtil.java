package internal.copy;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

interface JavaUtil {

    Map<Class<?>, Class<?>> primitiveEquivalentMapping = Map.of(
            boolean.class, Boolean.class,
            byte.class, Byte.class,
            short.class, Short.class,
            int.class, Integer.class,
            long.class, Long.class,
            float.class, Float.class,
            double.class, Double.class,
            char.class, Character.class
    );

    Set<Class<?>> primitiveEquivalentClasses = new HashSet<>(primitiveEquivalentMapping.values());

    Set<Class<?>> primitiveClasses = new HashSet<>(primitiveEquivalentMapping.keySet());

    static Optional<Class<?>> findEquivalent(Class<?> primitiveClazz) {
        return Optional.ofNullable(primitiveEquivalentMapping.get(primitiveClazz));
    }

    static boolean isPrimitive(Class<?> clazz) {
        return clazz.isPrimitive();
    }

    static boolean isPrimitiveEquivalent(Class<?> clazz) {
        return primitiveEquivalentClasses.contains(clazz);
    }
}
