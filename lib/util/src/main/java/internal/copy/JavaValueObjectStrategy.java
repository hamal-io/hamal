package internal.copy;

import internal.JavaReflection;
import io.hamal.lib.ddd.base.ValueObject;
import io.hamal.lib.meta.exception.InternalServerException;
import io.hamal.lib.meta.exception.NotImplementedYetException;

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
        var s = JavaReflection.Interfaces.implementsInterface(sourceClass, ValueObject.class);
        var t = JavaReflection.Interfaces.implementsInterface(targetClass, ValueObject.class);

        if (!s && !t) {
            return false;
        }

        if (sourceClass.equals(targetClass)) {
            var gs = ((ParameterizedType) sourceClass.getGenericSuperclass()).getActualTypeArguments()[0];
            var gt = ((ParameterizedType) targetClass.getGenericSuperclass()).getActualTypeArguments()[0];
            return copyStrategy.supports(gs.getClass()) && copyStrategy.supports(gt.getClass());
        }

        if (s == false && t == true) {
            var g = ((ParameterizedType) targetClass.getGenericSuperclass()).getActualTypeArguments()[0];
            return (sourceClass.equals(g) || promotePrimitiveIfPossible(sourceClass).equals(g));
        }

        if (s == true && t == false) {
            var g = ((ParameterizedType) sourceClass.getGenericSuperclass()).getActualTypeArguments()[0];
            return (targetClass.equals(g) || promotePrimitiveIfPossible(targetClass).equals(g));
        }

        if (s == true && t == true) {
            var ps = ((ParameterizedType) sourceClass.getGenericSuperclass()).getActualTypeArguments()[0];
            var pt = ((ParameterizedType) targetClass.getGenericSuperclass()).getActualTypeArguments()[0];

            return ps.equals(pt);
        }

        var g = ((ParameterizedType) targetClass.getGenericSuperclass()).getActualTypeArguments()[0];

        return copyStrategy.supports(sourceClass) && copyStrategy.supports(targetClass);
    }

    static class CopyStrategy implements JavaCopy.CopyStrategy {

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
                    .orElseThrow(() -> new InternalServerException("unable to apply any strategy", null));
        }
    }

    //    @Override
//    public boolean supports(Class<?> sourceClass, Class<?> targetClass) {
//        if (isValueObject(sourceClass)) {
//            return valueObjectSupports(sourceClass, targetClass);
//        } else if (isValueObject(targetClass)) {
//            return valueObjectSupports(targetClass, sourceClass);
//        }
//        return false;
//    }
//
//    private boolean valueObjectSupports(Class<?> valueObjectClass, Class<?> otherClass) {
//        if (isValueObject(otherClass)) {
//            return bothValueObjectsSupported(valueObjectClass, otherClass);
//        }
//
//        var valueType = resolveValueType(valueObjectClass);
//
//        var hasMatchingCtor = hasMatchingCtor(valueObjectClass, valueType);
//
//        otherClass = promotePrimitiveIfPossible(otherClass);
//
//        return valueType.equals(otherClass) && copyStrategy.supports(valueType) && hasMatchingCtor;
//    }
//
//    private boolean bothValueObjectsSupported(Class<?> left, Class<?> right) {
//        var leftValueType = resolveValueType(left);
//        var rightValueType = resolveValueType(right);
//        return leftValueType.equals(rightValueType) && copyStrategy.supports(leftValueType);
//    }
//
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

                if (hasSingleArgumentCtorWithArgumentClass(targetClass, normalizedSourceClass)) {
                    var ctor = getAccessibleConstructor(targetClass, normalizedSourceClass);
                    var instance = ctor.newInstance(copiedValue);

                    targetField.set(target, instance);
                } else {
                    throw new NotImplementedYetException();
                }

            }
            return true;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new InternalServerException(e);
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

    private boolean hasMatchingCtor(Class<?> targetClass, Class<?> argumentClass) {
        return hasDefaultCtor(targetClass) || hasSingleArgumentCtorWithArgumentClass(targetClass, argumentClass);
    }

    private boolean hasDefaultCtor(Class<?> targetClass) {
        return Arrays.stream(targetClass.getDeclaredConstructors())
                .anyMatch(ctor -> ctor.getParameterCount() == 0);
    }

    private boolean hasSingleArgumentCtorWithArgumentClass(Class<?> targetClass, Class<?> argumentClass) {
        return Arrays.stream(targetClass.getDeclaredConstructors())
                .filter(ctor -> ctor.getParameterCount() == 1)
                .anyMatch(ctor -> {
                    var paramType = ctor.getParameterTypes()[0];
                    return paramType.equals(argumentClass);
                });
    }

    private boolean isValueObject(Class<?> clazz) {
        return JavaReflection.Interfaces.implementsInterface(clazz, ValueObject.class);
    }
//
//    private Class<?> resolveValueType(Class<?> valueObjectClass) {
//        var inheritedFields = JavaReflection.Fields.inheritedDeclaredFieldsOf(valueObjectClass);
//
//        if (inheritedFields.size() != 2) {
//            throw new IllegalArgumentException("Expected " + valueObjectClass.getSimpleName() + " to have exactly one field");
//        }
//        return inheritedFields.get(0).getType();
//    }
//
    private Class<?> promotePrimitiveIfPossible(Class<?> maybePrimitiveClass) {
        return findEquivalent(maybePrimitiveClass).orElse(maybePrimitiveClass);
    }
}
