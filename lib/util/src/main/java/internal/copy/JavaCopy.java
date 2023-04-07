package internal.copy;

import internal.JavaReflection;
import io.hamal.lib.meta.exception.InternalServerException;
import io.hamal.lib.meta.exception.NotImplementedYetException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface JavaCopy {

    static Object copyValue(Object source, Class<?> sourceClass) {
        return copyValue(sourceClass, source, Set.of(
                new JavaPrimitiveStrategy(),
                new JavaEnumStrategy(),
                new JavaImmutableStrategy(JavaImmutableStrategy.defaultImmutableClasses)
        ));
    }

    static Object copyValue(Class<?> sourceClass, Object source, Collection<Strategy> strategies) {
        for (var strategy : strategies) {
            var copyStrategy = strategy.copyStrategy;
            if (copyStrategy.supports(sourceClass)) {
                return copyStrategy.apply(sourceClass, source);
            }
        }
        throw new NotImplementedYetException();
    }

//    public static <T> T copyFields(Object source, T target) {
//        return copyFields(source, target, Set.of(
//                new JavaPrimitiveFieldStrategy(),
//                new JavaImmutableFieldStrategy(JavaImmutableFieldStrategy.defaultImmutableClasses),
//                new JavaEnumFieldStrategy()
////                new ValueObjectFieldStrategy()
//        ));
//    }

    static <T> T copyFields(Object source, T target, Collection<Strategy> strategies) {

        if (source == null || target == null) {
            return target;
        }

        var sourceClass = source.getClass();
        var targetClass = target.getClass();

        var sourceFields = JavaReflection.Fields.inheritedDeclaredFieldsOf(sourceClass).stream()
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .peek(field -> field.setAccessible(true))
                .collect(Collectors.toSet());

        var targetFields = JavaReflection.Fields.inheritedDeclaredFieldsOf(targetClass).stream()
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .peek(field -> field.setAccessible(true))
                .collect(Collectors.toMap(Field::getName, Function.identity()));

        for (var sourceField : sourceFields) {
            var sourceName = sourceField.getName();

            if (!targetFields.containsKey(sourceName)) {
                continue;
            }
            var targetField = targetFields.get(sourceName);

            try {
                Object sourceValue = sourceField.get(source);
                if (sourceValue == null) {
                    continue;
                }

                var sourceFieldType = sourceField.getType();
                var targetFieldType = targetField.getType();

                for (Strategy strategy : strategies) {
                    if (strategy.supports(sourceFieldType, targetFieldType)) {
                        if (strategy.apply(target, sourceFieldType, sourceValue, targetFieldType, targetField)) {
                            break;
                        }
                    }

                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new InternalServerException(e);
            }
        }
        return target;
    }

    interface CopyStrategy {
        boolean supports(Class<?> valueClass);

        Object apply(Class<?> valueClass, Object value);
    }

    abstract class Strategy {

        protected final JavaCopy.CopyStrategy copyStrategy;

        protected Strategy(JavaCopy.CopyStrategy copyStrategy) {
            this.copyStrategy = copyStrategy;
        }

        abstract public boolean supports(Class<?> sourceClass, Class<?> targetClass);

        public boolean apply(Object target, Class<?> sourceClass, Object sourceValue, Class<?> targetClass, Field targetField) {
            try {
                targetField.set(target, copyStrategy.apply(sourceClass, sourceValue));
                return true;
            } catch (IllegalAccessException e) {
                throw new InternalServerException(e);
            }
        }
    }

//
//    class MultiCopyStrategy implements CopyStrategy {
//
//        private final List<CopyStrategy> strategies;
//
//        public MultiCopyStrategy(List<CopyStrategy> strategies) {
//            this.strategies = strategies;
//        }
//
//        @Override
//        public boolean supports(Class<?> valueClass) {
//            return strategies.stream()
//                    .anyMatch(strategy -> strategy.supports(valueClass));
//        }
//
//        @Override
//        public Object apply(Class<?> valueClass, Object value) {
//            return strategies.stream()
//                    .filter(strategy -> strategy.supports(valueClass))
//                    .map(strategy -> strategy.apply(valueClass, value))
//                    .findFirst()
//                    .orElseThrow(() -> new InternalServerException("unable to apply any strategy", null));
//        }
//
//        @Override
//        public FieldStrategy fieldStrategy() {
//            throw new NotImplementedYetException();
//        }
//    }
}
