package internal.copy;

import internal.JavaReflection;
import io.hamal.lib.ddd.base.ValueObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;

import static internal.copy.JavaCopy.copyValue;
import static internal.copy.JavaUtil.findEquivalent;
import static internal.copy.JavaUtil.isPrimitive;

/*
    cases:
    primitive -> ValueObject<Equivalent>
    Equivalent -> ValueObject<Equivalent>
    ValueObject<T> -> ValueObject<T>
    ValueObject<Equivalent> -> primitive
    ValueObject<Equivalent> -> Equivalent
 */
public class JavaValueObjectStrategy extends JavaCopy.Strategy {

    public JavaValueObjectStrategy(CopyStrategy delegate) {
        super(delegate);
    }

    @Override
    public boolean supports(Class<?> sourceClass, Class<?> targetClass) {
        var sourceIsValueObject = JavaReflection.Interfaces.implementsInterface(sourceClass, ValueObject.class);
        var targetIsValueObject = JavaReflection.Interfaces.implementsInterface(targetClass, ValueObject.class);

        if (!sourceIsValueObject && !targetIsValueObject) {
            return false;
        }

        if (sourceClass.equals(targetClass)) {
            return underlyingTypeIsSupported(sourceClass);
        }

        if (sourceIsValueObject && targetIsValueObject) {
            var sourceUnderlyingType = ((ParameterizedType) sourceClass.getGenericSuperclass()).getActualTypeArguments()[0];
            var targetUnderlyingType = ((ParameterizedType) targetClass.getGenericSuperclass()).getActualTypeArguments()[0];
            return sourceUnderlyingType.equals(targetUnderlyingType);
        }

        if (!sourceIsValueObject) {
            return underlyingTypeIsSupported(targetClass);
        }

        return underlyingTypeIsSupported(sourceClass);
    }

    private boolean underlyingTypeIsSupported(Class<?> clazz) {
        var underlyingClass = ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];
        return copyStrategy.supports(promotePrimitiveIfPossible((Class<?>) underlyingClass));
    }

    @Override
    public boolean apply(Object target, Class<?> sourceClass, Object sourceValue, Class<?> targetClass, Field targetField) {
        try {
            if (isValueObject(sourceClass)) {
                var valueMethodFromInterface = sourceClass.getMethod("getValue");
                var value = valueMethodFromInterface.invoke(sourceValue);

                var copiedValue = copyValue(value, value.getClass());

                if (isValueObject(targetClass)) {
                    var ctor = getAccessibleConstructor(targetClass, value.getClass());
                    var instance = ctor.newInstance(copiedValue);

                    targetField.set(target, instance);

                } else {
                    targetField.set(target, value);
                }

            } else {
                Class<?> normalizedSourceClass = findEquivalent(sourceClass).orElse(sourceClass);
                var copiedValue = copyValue(sourceValue, normalizedSourceClass);

                var ctor = getAccessibleConstructor(targetClass, normalizedSourceClass);
                var instance = ctor.newInstance(copiedValue);
                targetField.set(target, instance);
            }
            return true;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private Constructor<?> getAccessibleConstructor(Class<?> targetClass, Class<?> parameterClass) {
        return Arrays.stream(targetClass.getDeclaredConstructors())
                .filter(ctor -> ctor.getParameterCount() == 1)
                .filter(ctor -> {
                    var ctorParameterClass = ctor.getParameterTypes()[0];
                    if (ctorParameterClass == parameterClass) {
                        return true;
                    }

                    if (isPrimitive(ctorParameterClass)) {
                        var equivalent = findEquivalent(ctorParameterClass)
                                .orElseThrow(() -> new IllegalStateException("unable to find equivalent for primitive " + ctorParameterClass, null));

                        return equivalent.equals(parameterClass);
                    }
                    return false;
                })
                .peek(ctor -> ctor.setAccessible(true))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("unable to find suitable constructor", null));
    }

    private boolean isValueObject(Class<?> clazz) {
        return JavaReflection.Interfaces.implementsInterface(clazz, ValueObject.class);
    }

    private Class<?> promotePrimitiveIfPossible(Class<?> maybePrimitiveClass) {
        return findEquivalent(maybePrimitiveClass).orElse(maybePrimitiveClass);
    }

    public static class CopyStrategy implements JavaCopy.CopyStrategy {

        private final List<? extends JavaCopy.Strategy> strategies;

        public CopyStrategy(List<? extends JavaCopy.Strategy> strategies) {
            this.strategies = strategies;
        }

        @Override
        public boolean supports(Class<?> valueClass) {
            return strategies.stream()
                    .anyMatch(strategy -> strategy.copyStrategy.supports(valueClass));
        }

        @Override
        public Object apply(Class<?> valueClass, Object value) {
            return strategies.stream()
                    .filter(strategy -> strategy.copyStrategy.supports(valueClass))
                    .map(strategy -> strategy.copyStrategy.apply(valueClass, value))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Unable to apply any strategy", null));
        }
    }
}
