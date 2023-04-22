package internal.copy;

import io.hamal.lib.ddd.base.ValueObject;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JavaValueObjectStrategy")
class JavaValueObjectStrategyTest {

    private final JavaValueObjectStrategy testInstance = new JavaValueObjectStrategy(
            new JavaValueObjectStrategy.CopyStrategy(
                    Arrays.<JavaCopy.Strategy>asList(
                            new JavaPrimitiveStrategy(),
                            new JavaEnumStrategy(),
                            new JavaImmutableStrategy(new HashSet<>(
                            ))
                    )
            )
    );

    @Nested
    @DisplayName("supports()")
    class SupportsTest {
        @Test
        @DisplayName("source and target are not value objects")
        void bothNotValueObjects() {
            assertFalse(testInstance.supports(Object.class, Object.class));
        }

        @Test
        @DisplayName("primitive -> ValueObject<Equivalent> and safe copyable")
        void primitiveValueObject() {
            assertTrue(testInstance.supports(int.class, SomeIntValueObject.class));
        }

        @Test
        @DisplayName("Equivalent -> ValueObject<Equivalent> and safe copyable")
        void equivalentValueObject() {
            assertTrue(testInstance.supports(Integer.class, SomeIntValueObject.class));
        }

        @Test
        @DisplayName("T -> ValueObject<T> and safe copyable")
        void someSafeValueAndValueObject() {
            assertTrue(testInstance.supports(Instant.class, SomeInstantValueObject.class));
        }

        @Test
        @DisplayName("ValueObject<T> -> T  and safe copyable")
        void valueObjectAndSomeSafeObject() {
            assertTrue(testInstance.supports(SomeInstantValueObject.class, Instant.class));
        }

        @Test
        @DisplayName("ValueObject<T> -> ValueObject<T> and safe copyable")
        void sameValueObjectType() {
            assertTrue(testInstance.supports(SomeIntValueObject.class, SomeIntValueObject.class));
        }

        @Test
        @DisplayName("ValueObject<X> -> ValueObject<X> where X is not safe")
        void sourceNotSafeToCopy() {
            assertFalse(testInstance.supports(SomeUnsafeToCopyValueObject.class, SomeUnsafeToCopyValueObject.class));
        }

        @Test
        @DisplayName("X -> ValueObject<X> where X is not safe")
        void unsafeObjectSourceAndUnsafeValueObjectTarget() {
            assertFalse(testInstance.supports(SomeUnsafeClass.class, SomeUnsafeToCopyValueObject.class));
        }

        @Test
        @DisplayName("ValueObject<X> -> X where X is not safe")
        void someUnsafeValueObjectAndUnsafeObject() {
            assertFalse(testInstance.supports(SomeUnsafeToCopyValueObject.class, SomeUnsafeClass.class));
        }

        @Test
        @DisplayName("ValueObject<X> -> ValueObject<T> and safe copyable")
        void differentUnderlyingValueTypes() {
            assertFalse(testInstance.supports(SomeIntValueObject.class, SomeInstantValueObject.class));
        }

        @Test
        @DisplayName("ValueObject<Equivalent> -> primitive and safe copyable")
        void valueObjectPrimitive() {
            assertTrue(testInstance.supports(SomeIntValueObject.class, int.class));
        }

        @Test
        @DisplayName("ValueObject<Equivalent> -> Equivalent and safe copyable")
        void valueObjectEquivalent() {
            assertTrue(testInstance.supports(SomeIntValueObject.class, Integer.class));
        }
    }

    @Nested
    @DisplayName("apply()")
    class ApplyTest {

        @Test
        @DisplayName("primitive -> ValueObject<Equivalent> and safe copyable")
        void primitiveValueObject() {
            var source = new IntegerPrimitiveSource();
            var target = new ValueObjectTarget();

            JavaCopy.copyFields(source, target, Set.of(testInstance));

            assertThat(target.svo.getValue(), is(42));
        }

        @Test
        @DisplayName("Equivalent -> ValueObject<Equivalent> and safe copyable")
        void equivalentValueObject() {
            var source = new IntegerSource();
            var target = new ValueObjectTarget();

            JavaCopy.copyFields(source, target, Set.of(testInstance));

            assertThat(target.svo.getValue(), is(42));
        }

        @Test
        @DisplayName("T -> ValueObject<T> and safe copyable")
        void someSafeValueAndValueObject() {
            var source = new InstantSource();
            var target = new InstantValueObjectTarget();

            JavaCopy.copyFields(source, target, Set.of(testInstance));

            assertThat(target.value.getValue(), is(Instant.MIN));
        }

        @Test
        @DisplayName("ValueObject<T> -> T  and safe copyable")
        void valueObjectAndSomeSafeObject() {
            var source = new InstantValueObjectSource();
            var target = new InstantTarget();

            JavaCopy.copyFields(source, target, Set.of(testInstance));

            assertThat(target.value, is(Instant.MIN));
        }

        @Test
        @DisplayName("ValueObject<T> -> ValueObject<T> and safe copyable")
        void sameValueObjectType() {
            var source = new ValueObjectSource();
            var target = new ValueObjectTarget();

            JavaCopy.copyFields(source, target, Set.of(testInstance));

            assertNotNull(target.svo);
            assertThat(target.svo.getValue(), is(42));
        }

        @Test
        @DisplayName("ValueObject<Equivalent> -> primitive and safe copyable")
        void valueObjectPrimitive() {
            var source = new ValueObjectSource();
            var target = new IntegerPrimitiveTarget();

            JavaCopy.copyFields(source, target, Set.of(testInstance));

            assertThat(target.svo, is(42));
        }

        @Test
        @DisplayName("ValueObject<Equivalent> -> Equivalent and safe copyable")
        void valueObjectEquivalent() {
            var source = new ValueObjectSource();
            var target = new IntegerTarget();

            JavaCopy.copyFields(source, target, Set.of(testInstance));

            assertNotNull(target.svo);
            assertThat(target.svo, is(42));
        }

        @Test
        @DisplayName("reproduce and fix bug - value object contains static fields")
        void valueObjectContainsStaticFieldsBug() {
            var source = new SomClassWithStaticFieldsSource();
            var target = new SomClassWithStaticFieldsTarget();

            JavaCopy.copyFields(source, target, Set.of(testInstance));

            assertNotNull(target.value);
            assertThat(target.value, is(SomClassWithStaticFields.of(Instant.ofEpochSecond(164534232))));
        }
    }

    static class SomeUnsafeClass {
        List<Integer> values = Arrays.asList(1, 2, 3, 4);
    }

    static class SomeUnsafeToCopyValueObject extends ValueObject.BaseImpl<SomeUnsafeClass> {
        public SomeUnsafeToCopyValueObject() {
        }

        @NotNull
        @Override
        public SomeUnsafeClass getValue() {
            return new SomeUnsafeClass();
        }
    }

    static class SomeIntValueObject extends ValueObject.ComparableImpl<Integer> {
        private final Integer value;

        SomeIntValueObject(Integer value) {
            this.value = value;
        }

        @NotNull
        @Override
        public Integer getValue() {
            return value;
        }
    }

    static class SomeInstantValueObject extends ValueObject.ComparableImpl<Instant> {
        private final Instant value;

        SomeInstantValueObject(Instant value) {
            this.value = value;
        }

        @NotNull
        @Override
        public Instant getValue() {
            return value;
        }
    }

    static class InstantSource {
        private final Instant value = Instant.MIN;
    }

    static class InstantValueObjectSource {
        SomeInstantValueObject value = new SomeInstantValueObject(Instant.MIN);
    }

    static class ValueObjectSource {
        SomeIntValueObject svo = new SomeIntValueObject(42);
    }

    static class InstantValueObjectTarget {
        SomeInstantValueObject value;
    }

    static class InstantTarget {
        Instant value;
    }

    static class IntegerSource {
        Integer svo = 42;
    }

    static class IntegerPrimitiveSource {
        int svo = 42;
    }

    static class ValueObjectTarget {
        SomeIntValueObject svo;
    }

    static class IntegerTarget {
        Integer svo;
    }

    static class IntegerPrimitiveTarget {
        int svo;
    }

    static class SomClassWithStaticFields extends ValueObject.BaseImpl<Instant> {

        public static final SomClassWithStaticFields MIN = of(Instant.MIN);

        public static final SomClassWithStaticFields MAX = of(Instant.MAX);

        public static SomClassWithStaticFields of(Instant value) {
            return new SomClassWithStaticFields(value);
        }

        private final Instant value;

        private SomClassWithStaticFields(Instant value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SomClassWithStaticFields that = (SomClassWithStaticFields) o;
            return Objects.equals(getValue(), that.getValue());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getValue());
        }

        @NotNull
        @Override
        public Instant getValue() {
            return value;
        }
    }

    static class SomClassWithStaticFieldsSource {
        SomClassWithStaticFields value = SomClassWithStaticFields.of(Instant.ofEpochSecond(164534232));
    }

    static class SomClassWithStaticFieldsTarget {
        SomClassWithStaticFields value;
    }
}





