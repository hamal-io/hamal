package internal.copy;

import internal.copy.JavaCopy;
import internal.copy.JavaPrimitiveStrategy;
import io.hamal.lib.Tuple2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JavaPrimitiveStrategy")
class JavaPrimitiveStrategyTest {

    private final JavaPrimitiveStrategy testInstance = new JavaPrimitiveStrategy();

    @Nested
    @DisplayName("CopyStrategy")
    class CopyStrategyTest {
        class JavaPrimitiveCopyStrategyTest {

            private final JavaPrimitiveStrategy.CopyStrategy testInstance = new JavaPrimitiveStrategy.CopyStrategy();

            @Nested
            @DisplayName("supports()")
            class SupportsTest {

                private static Stream<Class<?>> primitiveAndEquivalentClassProvider() {
                    return Stream.of(
                            boolean.class, Boolean.class,
                            byte.class, Byte.class,
                            short.class, Short.class,
                            int.class, Integer.class,
                            long.class, Long.class,
                            float.class, Float.class,
                            double.class, Double.class,
                            char.class, Character.class
                    );
                }

                @DisplayName("supported combinations")
                @ParameterizedTest(name = "#{index} - {0}")
                @MethodSource("primitiveAndEquivalentClassProvider")
                void unsupportedCombinations(Class<?> arg) {
                    assertTrue(testInstance.supports(arg));
                }
            }

            @Nested
            @DisplayName("apply()")
            class ApplyTest {

                @Test
                @DisplayName("primitive boolean")
                void primitiveBoolean() {
                    boolean someValue = true;
                    var result = testInstance.apply(boolean.class, someValue);
                    assertThat(result, is(true));
                    assertEquals(result.getClass(), Boolean.class);
                }

                @Test
                @DisplayName("boolean object")
                void booleanObject() {
                    Boolean someValue = true;
                    var result = testInstance.apply(Boolean.class, someValue);
                    assertSame(someValue, result);
                }

                @Test
                @DisplayName("primitive byte")
                void primitiveByte() {
                    byte someValue = 127;
                    var result = testInstance.apply(byte.class, someValue);
                    assertThat(result, is((byte) 127));
                    assertEquals(result.getClass(), Byte.class);
                }

                @Test
                @DisplayName("byte object")
                void byteObject() {
                    Byte someValue = 127;
                    var result = testInstance.apply(Byte.class, someValue);
                    assertSame(someValue, result);
                }

                @Test
                @DisplayName("primitive short")
                void primitiveShort() {
                    short someValue = 42;
                    var result = testInstance.apply(short.class, someValue);
                    assertThat(result, is((short) 42));
                    assertEquals(result.getClass(), Short.class);
                }

                @Test
                @DisplayName("short object")
                void shortObject() {
                    Short someValue = 42;
                    var result = testInstance.apply(Short.class, someValue);
                    assertSame(someValue, result);
                }

                @Test
                @DisplayName("primitive int")
                void primitiveInt() {
                    int someValue = 42;
                    var result = testInstance.apply(int.class, someValue);
                    assertThat(result, is(42));
                    assertEquals(result.getClass(), Integer.class);
                }

                @Test
                @DisplayName("int object")
                void intObject() {
                    Integer someValue = 42;
                    var result = testInstance.apply(Integer.class, someValue);
                    assertSame(someValue, result);
                }

                @Test
                @DisplayName("primitive float")
                void primitiveFloat() {
                    float someValue = 42.f;
                    var result = testInstance.apply(float.class, someValue);
                    assertThat(result, is(42.f));
                    assertEquals(result.getClass(), Float.class);
                }

                @Test
                @DisplayName("float object")
                void floatObject() {
                    Float someValue = 42.f;
                    var result = testInstance.apply(Float.class, someValue);
                    assertSame(someValue, result);
                }

                @Test
                @DisplayName("primitive double")
                void primitiveDouble() {
                    double someValue = 42.d;
                    var result = testInstance.apply(double.class, someValue);
                    assertThat(result, is(42.d));
                    assertEquals(result.getClass(), Double.class);
                }

                @Test
                @DisplayName("double object")
                void doubleObject() {
                    Double someValue = 42.d;
                    var result = testInstance.apply(Double.class, someValue);
                    assertSame(someValue, result);
                }

                @Test
                @DisplayName("primitive char")
                void primitiveChar() {
                    char someValue = '*';
                    var result = testInstance.apply(char.class, someValue);
                    assertThat(result, is('*'));
                    assertEquals(result.getClass(), Character.class);
                }

                @Test
                @DisplayName("character object")
                void characterObject() {
                    Character someValue = '*';
                    var result = testInstance.apply(Character.class, someValue);
                    assertSame(someValue, result);
                }
            }
        }
    }

    @Nested
    @DisplayName("supports()")
    class SupportsTest {
        @Test
        @DisplayName("both are primitive")
        void bothPrimitive() {
            assertTrue(testInstance.supports(int.class, int.class));
        }

        @Test
        @DisplayName("both are primitive equivalents")
        void bothAreEquivalentsPrimitives() {
            assertTrue(testInstance.supports(Integer.class, Integer.class));
        }

        @Test
        @DisplayName("both are different objects")
        void bothDifferentObjects() {
            assertFalse(testInstance.supports(Object.class, Object.class));
        }

        @Test
        @DisplayName("primitive source and equivalent target")
        void sourcePrimitiveTargetEquivalent() {
            assertTrue(testInstance.supports(int.class, Integer.class));
        }

        @Test
        @DisplayName("primitive source and object target")
        void sourcePrimitiveTargetObject() {
            assertFalse(testInstance.supports(int.class, Object.class));
        }

        @Test
        @DisplayName("equivalent source and object target")
        void sourceEquivalentTargetObject() {
            assertFalse(testInstance.supports(Integer.class, Object.class));
        }

        @Test
        @DisplayName("object source and primitive target")
        void sourceObjectTargetPrimitive() {
            assertFalse(testInstance.supports(Object.class, int.class));
        }

        @Test
        @DisplayName("object source and equivalent target")
        void sourceObjectTargetEquivalent() {
            assertFalse(testInstance.supports(Object.class, Integer.class));
        }

        @Test
        @DisplayName("equivalent source and primitive target")
        void sourceEquivalentTargetPrimitive() {
            assertTrue(testInstance.supports(Integer.class, int.class));
        }

        private static Stream<Tuple2<Class<?>, Class<?>>> primitiveEquivalentClassProvider() {
            return Stream.of(
                    new Tuple2<>(boolean.class, Boolean.class),
                    new Tuple2<>(byte.class, Byte.class),
                    new Tuple2<>(short.class, Short.class),
                    new Tuple2<>(int.class, Integer.class),
                    new Tuple2<>(long.class, Long.class),
                    new Tuple2<>(float.class, Float.class),
                    new Tuple2<>(double.class, Double.class),
                    new Tuple2<>(char.class, Character.class)
            );
        }

        @DisplayName("primitive primitive combinations")
        @ParameterizedTest(name = "#{index} - {0}")
        @MethodSource("primitiveEquivalentClassProvider")
        void primitivePrimitiveCombinations(Tuple2<Class<?>, Class<?>> arg) {
            var somePrimitive = arg.get_1();

            assertTrue(testInstance.supports(somePrimitive, somePrimitive));
        }

        @DisplayName("equivalent primitive combinations")
        @ParameterizedTest(name = "#{index} - {0}")
        @MethodSource("primitiveEquivalentClassProvider")
        void equivalentPrimitive(Tuple2<Class<?>, Class<?>> arg) {
            var somePrimitive = arg.get_1();
            var someEquivalent = arg.get_2();

            assertTrue(testInstance.supports(someEquivalent, somePrimitive));
        }

        @DisplayName("primitive equivalent combinations")
        @ParameterizedTest(name = "#{index} - {0}")
        @MethodSource("primitiveEquivalentClassProvider")
        void primitiveEquivalent(Tuple2<Class<?>, Class<?>> arg) {
            var somePrimitive = arg.get_1();
            var someEquivalent = arg.get_2();

            assertTrue(testInstance.supports(somePrimitive, someEquivalent));
        }

        @DisplayName("equivalent equivalent combinations")
        @ParameterizedTest(name = "#{index} - {0}")
        @MethodSource("primitiveEquivalentClassProvider")
        void equivalentEquivalent(Tuple2<Class<?>, Class<?>> arg) {
            var someEquivalent = arg.get_2();
            assertTrue(testInstance.supports(someEquivalent, someEquivalent));
        }
    }

    @Nested
    @DisplayName("apply()")
    class ApplyTest {

        class PrimitiveSource {
            boolean aBoolean = true;
            byte aByte = 1;
            short aShort = 2;
            int anInt = 3;
            long aLong = 4;
            float aFloat = 5.5f;
            double aDouble = 6.6d;
            char aChar = '*';
        }

        class EquivalentSource {
            Boolean aBoolean = true;
            Byte aByte = 100;
            Short aShort = 101;
            Integer anInt = 102;
            Long aLong = 103L;
            Float aFloat = 104.104f;
            Double aDouble = 105.105d;
            Character aChar = '$';
        }

        class PrimitiveTarget {
            boolean aBoolean;
            byte aByte;
            short aShort;
            int anInt;
            long aLong;
            float aFloat;
            double aDouble;
            char aChar;
        }

        class EquivalentTarget {
            Boolean aBoolean;
            Byte aByte;
            Short aShort;
            Integer anInt;
            Long aLong;
            Float aFloat;
            Double aDouble;
            Character aChar;
        }

        class NotMatchingPrimitiveTarget {
            byte aBoolean;
            short aByte;
            int aShort;
            long anInt;
            float aLong;
            double aFloat;
            char aDouble;
            boolean aChar;
        }

        class NotMatchingEquivalentTarget {
            Byte aBoolean;
            Short aByte;
            Integer aShort;
            Long anInt;
            Float aLong;
            Double aFloat;
            Character aDouble;
            Boolean aChar;
        }

        @Test
        @DisplayName("copy primitive source to primitive target")
        void primitiveSourcePrimitiveTarget() {
            var source = new PrimitiveSource();
            var target = new PrimitiveTarget();

            JavaCopy.copyFields(source, target, Set.of(testInstance));

            assertSourceHasNotChanged(source);
            assertTargetHasChangedByPrimitiveSource(target);
        }

        @Test
        @DisplayName("copy equivalent source to equivalent target")
        void equivalentSourceToEquivalentTarget() {
            var source = new EquivalentSource();
            var target = new EquivalentTarget();

            JavaCopy.copyFields(source, target, Set.of(testInstance));

            assertSourceHasNotChanged(source);
            assertTargetHasChangedByEquivalentSource(target);
        }

        @Test
        @DisplayName("copy primitive to source to equivalent target")
        void primitiveSourceEquivalentTarget() {
            var source = new EquivalentSource();
            var target = new PrimitiveTarget();

            JavaCopy.copyFields(source, target, Set.of(testInstance));

            assertSourceHasNotChanged(source);
            assertTargetHasChangedByEquivalentSource(target);
        }

        @Test
        @DisplayName("copy primitive source to equivalent target")
        void primitiveSourceToEquivalentTarget() {
            var source = new PrimitiveSource();
            var target = new EquivalentTarget();

            JavaCopy.copyFields(source, target, Set.of(testInstance));

            assertSourceHasNotChanged(source);
            assertTargetHasChangedByPrimitiveSource(target);
        }

        @Test
        @DisplayName("copy primitive fields only if types are matching")
        void primitiveValuesAreNotMatching() {
            var source = new PrimitiveSource();
            var target = new NotMatchingPrimitiveTarget();

            JavaCopy.copyFields(source, target, Set.of(testInstance));

            assertSourceHasNotChanged(source);
            assertTargetHasNullValues(target);
        }

        @Test
        @DisplayName("copy equivalent fields only if types are matching")
        void equivalentValuesAreNotMatching() {
            var source = new PrimitiveSource();
            var target = new NotMatchingEquivalentTarget();

            JavaCopy.copyFields(source, target, Set.of(testInstance));

            assertSourceHasNotChanged(source);
            assertTargetHasNullValues(target);
        }

        private void assertSourceHasNotChanged(PrimitiveSource source) {
            assertThat(source.aBoolean, is(true));
            assertThat(source.aByte, is((byte) 1));
            assertThat(source.aShort, is((short) 2));
            assertThat(source.anInt, is(3));
            assertThat(source.aLong, is(4L));
            assertThat(source.aFloat, is(5.5f));
            assertThat(source.aDouble, is(6.6d));
            assertThat(source.aChar, is('*'));
        }

        private void assertSourceHasNotChanged(EquivalentSource source) {
            assertThat(source.aBoolean, is(true));
            assertThat(source.aByte, is((byte) 100));
            assertThat(source.aShort, is((short) 101));
            assertThat(source.anInt, is(102));
            assertThat(source.aLong, is(103L));
            assertThat(source.aFloat, is(104.104f));
            assertThat(source.aDouble, is(105.105d));
            assertThat(source.aChar, is('$'));
        }

        private void assertTargetHasChangedByPrimitiveSource(PrimitiveTarget target) {
            assertThat(target.aBoolean, is(true));
            assertThat(target.aByte, is((byte) 1));
            assertThat(target.aShort, is((short) 2));
            assertThat(target.anInt, is(3));
            assertThat(target.aLong, is(4L));
            assertThat(target.aFloat, is(5.5f));
            assertThat(target.aDouble, is(6.6d));
            assertThat(target.aChar, is('*'));
        }

        private void assertTargetHasChangedByPrimitiveSource(EquivalentTarget target) {
            assertThat(target.aBoolean, is(true));
            assertThat(target.aByte, is((byte) 1));
            assertThat(target.aShort, is((short) 2));
            assertThat(target.anInt, is(3));
            assertThat(target.aLong, is(4L));
            assertThat(target.aFloat, is(5.5f));
            assertThat(target.aDouble, is(6.6d));
            assertThat(target.aChar, is('*'));
        }


        private void assertTargetHasChangedByEquivalentSource(EquivalentTarget target) {
            assertThat(target.aBoolean, is(true));
            assertThat(target.aByte, is((byte) 100));
            assertThat(target.aShort, is((short) 101));
            assertThat(target.anInt, is(102));
            assertThat(target.aLong, is(103L));
            assertThat(target.aFloat, is(104.104f));
            assertThat(target.aDouble, is(105.105d));
            assertThat(target.aChar, is('$'));
        }

        private void assertTargetHasChangedByEquivalentSource(PrimitiveTarget target) {
            assertThat(target.aBoolean, is(true));
            assertThat(target.aByte, is((byte) 100));
            assertThat(target.aShort, is((short) 101));
            assertThat(target.anInt, is(102));
            assertThat(target.aLong, is(103L));
            assertThat(target.aFloat, is(104.104f));
            assertThat(target.aDouble, is(105.105d));
            assertThat(target.aChar, is('$'));
        }

        private void assertTargetHasNullValues(NotMatchingPrimitiveTarget target) {
            assertThat(target.aByte, is((short) 0));
            assertThat(target.aShort, is(0));
            assertThat(target.anInt, is(0L));
            assertThat(target.aLong, is(0f));
            assertThat(target.aFloat, is(0d));
            assertThat(target.aDouble, is((char) 0));
            assertThat(target.aChar, is(false));
            assertThat(target.aBoolean, is((byte) 0));
        }

        private void assertTargetHasNullValues(NotMatchingEquivalentTarget target) {
            assertNull(target.aBoolean);
            assertNull(target.aByte);
            assertNull(target.aShort);
            assertNull(target.anInt);
            assertNull(target.aLong);
            assertNull(target.aFloat);
            assertNull(target.aDouble);
            assertNull(target.aChar);
        }
    }
}