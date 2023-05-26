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


class JavaEnumStrategyTest {

    private final JavaEnumStrategy testInstance = new JavaEnumStrategy();

    @Nested
    
    class CopyStrategyTest {

        private final JavaEnumStrategy.CopyStrategy testInstance = new JavaEnumStrategy.CopyStrategy();

        @Nested
        
        class SupportsTest {
            @Test
            
            void ok() {
                assertTrue(testInstance.supports(SomeTestEnum.class));
            }

            @Test
            
            void sourceEnumTargetNot() {
                assertFalse(testInstance.supports(Object.class));
            }
        }

        @Nested
        
        class ApplyTest {
            @Test
            
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
    
    class SupportsTest {

        @Test
        
        void bothAreNotEnums() {
            assertFalse(testInstance.supports(Object.class, Object.class));
        }

        @Test
        
        void sourceEnumTargetNot() {
            assertFalse(testInstance.supports(TestEnum.class, Object.class));
        }

        @Test
        
        void targetEnumButSourceNot() {
            assertFalse(testInstance.supports(Object.class, TestEnum.class));
        }

        @Test
        
        void sourceAndTargetDifferentEnumTypes() {
            assertFalse(testInstance.supports(TestEnum.class, AnotherEnum.class));
            assertFalse(testInstance.supports(AnotherEnum.class, TestEnum.class));
        }

        @Test
        
        void ok() {
            assertTrue(testInstance.supports(TestEnum.class, TestEnum.class));
            assertTrue(testInstance.supports(AnotherEnum.class, AnotherEnum.class));
        }
    }

    @Nested
    
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
        
        void ok() {
            var source = new EnumSource();
            var target = new EnumTarget();

            JavaCopy.copyFields(source, target, Set.of(testInstance));

            assertSourceHasNotChanged(source);
            assertTargetHasChanged(target);
        }

        @Test
        
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