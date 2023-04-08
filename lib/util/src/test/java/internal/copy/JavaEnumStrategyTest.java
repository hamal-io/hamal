package internal.copy;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static internal.copy.JavaEnumStrategyTest.AnotherEnum.ANOTHER_VALUE_1;
import static internal.copy.JavaEnumStrategyTest.TestEnum.VALUE_2;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JavaEnumStrategy")
class JavaEnumStrategyTest {

    private final JavaEnumStrategy testInstance = new JavaEnumStrategy();

    @Nested
    @DisplayName("CopyStrategy")
    class CopyStrategyTest {

        private final JavaEnumStrategy.CopyStrategy testInstance = new JavaEnumStrategy.CopyStrategy();

        @Nested
        @DisplayName("supports()")
        class SupportsTest {
            @Test
            @DisplayName("true if value class belongs to an enum")
            void ok() {
                assertTrue(testInstance.supports(SomeTestEnum.class));
            }

            @Test
            @DisplayName("not an enum")
            void sourceEnumTargetNot() {
                assertFalse(testInstance.supports(Object.class));
            }
        }

        @Nested
        @DisplayName("apply()")
        class ApplyTest {
            @Test
            @DisplayName("returns same instance")
            void ok() {
                var someValue = SomeTestEnum.VALUE_2;
                var result = testInstance.apply(SomeTestEnum.class, someValue);

                assertSame(someValue, result);
            }
        }

        enum SomeTestEnum {
            VALUE_1,
            VALUE_2,
            VALUE_3,
        }
    }

    @Nested
    @DisplayName("supports()")
    class SupportsTest {

        @Test
        @DisplayName("both are not enums")
        void bothAreNotEnums() {
            assertFalse(testInstance.supports(Object.class, Object.class));
        }

        @Test
        @DisplayName("source is enum but target is not")
        void sourceEnumTargetNot() {
            assertFalse(testInstance.supports(TestEnum.class, Object.class));
        }

        @Test
        @DisplayName("source is not enum but target is")
        void targetEnumButSourceNot() {
            assertFalse(testInstance.supports(Object.class, TestEnum.class));
        }

        @Test
        @DisplayName("source and target belong to different enums")
        void sourceAndTargetDifferentEnumTypes() {
            assertFalse(testInstance.supports(TestEnum.class, AnotherEnum.class));
            assertFalse(testInstance.supports(AnotherEnum.class, TestEnum.class));
        }

        @Test
        @DisplayName("source and target belong to the same enum")
        void ok() {
            assertTrue(testInstance.supports(TestEnum.class, TestEnum.class));
            assertTrue(testInstance.supports(AnotherEnum.class, AnotherEnum.class));
        }
    }

    @Nested
    @DisplayName("apply()")
    class ApplyTest {

        static class EnumSource {
            TestEnum testEnum = VALUE_2;
            AnotherEnum anotherEnum = ANOTHER_VALUE_1;
        }

        static class EnumTarget {
            TestEnum testEnum;
            AnotherEnum anotherEnum;
        }

        static class AnotherEnumTarget {
            AnotherEnum testEnum;
            TestEnum anotherEnum;
        }

        @Test
        @DisplayName("copies enum fields from source to target")
        void ok() {
            var source = new EnumSource();
            var target = new EnumTarget();

            JavaCopy.copyFields(source, target, Set.of(testInstance));

            assertSourceHasNotChanged(source);
            assertTargetHasChanged(target);
        }

        @Test
        @DisplayName("copies enum fields only if type matches")
        void typesAreNotMatching() {
            var source = new EnumSource();
            var target = new AnotherEnumTarget();

            JavaCopy.copyFields(source, target, Set.of(testInstance));

            assertSourceHasNotChanged(source);
            assertTargetHasNotChanged(target);
        }

        private void assertSourceHasNotChanged(EnumSource source) {
            assertThat(source.testEnum, CoreMatchers.is(VALUE_2));
            assertThat(source.anotherEnum, CoreMatchers.is(ANOTHER_VALUE_1));
        }

        private void assertTargetHasChanged(EnumTarget target) {
            assertThat(target.testEnum, CoreMatchers.is(VALUE_2));
            assertThat(target.anotherEnum, CoreMatchers.is(ANOTHER_VALUE_1));
        }

        private void assertTargetHasNotChanged(AnotherEnumTarget target) {
            assertNull(target.testEnum);
            assertNull(target.anotherEnum);
        }
    }

    enum TestEnum {
        VALUE_1,
        VALUE_2,
        VALUE_3
    }

    enum AnotherEnum {
        ANOTHER_VALUE_1,
        ANOTHER_VALUE_2
    }
}