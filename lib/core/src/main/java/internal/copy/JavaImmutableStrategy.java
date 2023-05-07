package internal.copy;

import io.hamal.lib.core.ddd.base.ValueObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.HashSet;
import java.util.Set;

/**
 * There are immutable objects which are safe to just copy from source to target.
 * Like: Integer -> Integer, Instant -> Instant, BigDecimal -> BigDecimal, ...
 */
public class JavaImmutableStrategy extends JavaCopy.Strategy {

    static final Set<Class<?>> defaultImmutableClasses = Set.of(
            Boolean.class,
            Byte.class,
            Short.class,
            Integer.class,
            Long.class,
            Float.class,
            Double.class,
            Character.class,
            BigDecimal.class,
            BigInteger.class,
            Instant.class,
            LocalDate.class,
            LocalDateTime.class,
            LocalTime.class,
            Duration.class,
            String.class,
            ValueObject.class
    );

    private static Set<Class<?>> mergeWithDefaultImmutableClasses(Set<Class<?>> classes) {
        var result = new HashSet<Class<?>>();
        result.addAll(classes);
        result.addAll(defaultImmutableClasses);
        return result;
    }


    public JavaImmutableStrategy() {
        this(new HashSet<>());
    }

    public JavaImmutableStrategy(Set<Class<?>> immutableClasses) {
        super(new CopyStrategy(mergeWithDefaultImmutableClasses(immutableClasses)));
    }

    @Override
    public boolean supports(Class<?> sourceClass, Class<?> targetClass) {
        return sourceClass.equals(targetClass) && copyStrategy.supports(sourceClass);
    }

    public static class CopyStrategy implements JavaCopy.CopyStrategy {

        final Set<Class<?>> safeClasses;

        public CopyStrategy(Set<Class<?>> safeClasses) {
            this.safeClasses = safeClasses;
        }

        @Override
        public boolean supports(Class<?> valueClass) {
            return safeClasses.contains(valueClass);
        }

        @Override
        public Object apply(Class<?> valueClass, Object value) {
            return value;
        }
    }

}
