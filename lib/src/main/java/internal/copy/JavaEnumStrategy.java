package internal.copy;

/**
 * Copies values of an enum from source to target. Requires both enums to be of the same type.
 */
public class JavaEnumStrategy extends JavaCopy.Strategy {

    public JavaEnumStrategy() {
        super(new CopyStrategy());
    }

    @Override
    public boolean supports(Class<?> sourceClass, Class<?> targetClass) {
        return sourceClass.equals(targetClass) && copyStrategy.supports(sourceClass);
    }

    public static class CopyStrategy implements JavaCopy.CopyStrategy {

        @Override
        public boolean supports(Class<?> valueClass) {
            return valueClass.isEnum();
        }

        @Override
        public Object apply(Class<?> valueClass, Object value) {
            return value;
        }
    }

}
