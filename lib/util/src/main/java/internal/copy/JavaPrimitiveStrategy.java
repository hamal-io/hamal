package internal.copy;


/**
 * Copies a primitive source field to a primitive target field. Both fields have to be of the same type:
 * Like: int -> int, float -> float
 * But not: int -> float, float -> double
 */
public class JavaPrimitiveStrategy extends JavaCopy.Strategy {

    public JavaPrimitiveStrategy() {
        super(new CopyStrategy());
    }

    @Override
    public boolean supports(Class<?> sourceClass, Class<?> targetClass) {
        var bothPrimitive = JavaUtil.isPrimitive(sourceClass) && JavaUtil.isPrimitive(targetClass);
        var bothEquivalent = JavaUtil.isPrimitiveEquivalent(sourceClass) && JavaUtil.isPrimitiveEquivalent(targetClass);
        var bothAreEqual = sourceClass.equals(targetClass);

        if (bothPrimitive && bothAreEqual) {
            return copyStrategy.supports(sourceClass);
        }

        if (bothEquivalent && bothAreEqual) {
            return copyStrategy.supports(targetClass);
        }

        if (JavaUtil.isPrimitive(sourceClass)) {
            return JavaUtil.findEquivalent(sourceClass)
                    .filter(cls -> cls.equals(targetClass))
                    .isPresent();

        } else if (JavaUtil.isPrimitiveEquivalent(sourceClass)) {
            return JavaUtil.findEquivalent(targetClass)
                    .filter(cls -> cls.equals(sourceClass))
                    .isPresent();
        }

        return false;
    }


    public static class CopyStrategy implements JavaCopy.CopyStrategy {

        @Override
        public boolean supports(Class<?> valueClass) {
            return JavaUtil.isPrimitive(valueClass) || JavaUtil.isPrimitiveEquivalent(valueClass);
        }

        @Override
        public Object apply(Class<?> valueClass, Object value) {
            return value;
        }

    }

}
