package internal.copy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;


class JavaUtilTest {

    static Stream<Class<?>> primitives() {
        return Stream.of(
                boolean.class,
                byte.class,
                short.class,
                int.class,
                long.class,
                float.class,
                double.class,
                char.class
        );
    }

    
    @ParameterizedTest(name = "#{index} - {0}")
    @MethodSource("primitives")
    void primitives(Class<?> arg) {
        assertTrue(JavaUtil.primitiveClasses.contains(arg));
    }

    @Test
    
    void primitiveClassesSize() {
        assertThat(JavaUtil.primitiveClasses.size(), is(8));
    }

    
    @ParameterizedTest(name = "#{index} - {0}")
    @MethodSource("primitives")
    void equivalentMapped(Class<?> arg) {
        assertNotNull(JavaUtil.primitiveEquivalentMapping.get(arg));
    }

    @Nested
    
    class IsPrimitiveTest {
        static Stream<Class<?>> primitives() {
            return Stream.of(
                    boolean.class,
                    byte.class,
                    short.class,
                    int.class,
                    long.class,
                    float.class,
                    double.class,
                    char.class
            );
        }

        
        @ParameterizedTest(name = "#{index} - {0}")
        @MethodSource("primitives")
        void ok(Class<?> arg) {
            assertTrue(JavaUtil.isPrimitive(arg));
        }

        @Test
        
        void notPrimitive() {
            assertFalse(JavaUtil.isPrimitive(Integer.class));
        }
    }

    @Nested
    
    class IsEquivalentTest {
        static Stream<Class<?>> primitiveEquivalents() {
            return Stream.of(
                    Boolean.class,
                    Byte.class,
                    Short.class,
                    Integer.class,
                    Long.class,
                    Float.class,
                    Double.class,
                    Character.class
            );
        }

        
        @ParameterizedTest(name = "#{index} - {0}")
        @MethodSource("primitiveEquivalents")
        void ok(Class<?> arg) {
            assertTrue(JavaUtil.isPrimitiveEquivalent(arg));
        }

        @Test
        
        void notPrimitive() {
            assertFalse(JavaUtil.isPrimitiveEquivalent(int.class));
        }
    }
}