package internal.copy;

import internal.copy.JavaCopy;
import internal.copy.JavaImmutableStrategy;
import io.hamal.lib.ddd.base.ValueObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.Set;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JavaImmutableStrategy")
class JavaImmutableStrategyTest {

    private final JavaImmutableStrategy testInstance = new JavaImmutableStrategy();

    @Nested
    @DisplayName("CopyStrategy")
    class CopyStrategyTest {

        private final JavaImmutableStrategy.CopyStrategy testInstance = new JavaImmutableStrategy.CopyStrategy(JavaImmutableStrategy.defaultImmutableClasses);

        @Test
        @DisplayName("17 default classes are considered being safe to copy")
        void safeClassesCount() {
            assertThat(JavaImmutableStrategy.defaultImmutableClasses, hasSize(17));
        }

        @Nested
        @DisplayName("supports()")
        class SupportsTest {

            @Test
            @DisplayName("unsafe object")
            void bothAreNotSafe() {
                assertFalse(testInstance.supports(Object.class));
            }

            static Stream<Class<?>> safeClassProvider() {
                return Stream.of(
                        Boolean.class,
                        Byte.class,
                        Short.class,
                        Integer.class,
                        Long.class,
                        Float.class,
                        Double.class,
                        Character.class,
                        BigDecimal.class,
                        BigInteger.class,
                        Instant.class,
                        LocalDate.class,
                        LocalDateTime.class,
                        LocalTime.class,
                        Duration.class,
                        String.class,
                        ValueObject.class
                );
            }

            @DisplayName("safe classes")
            @ParameterizedTest(name = "#{index} - {0}")
            @MethodSource("safeClassProvider")
            void safeClasses(Class<?> arg) {
                assertTrue(testInstance.supports(arg));
            }
        }

        @Nested
        @DisplayName("apply()")
        class ApplyTest {
            @Test
            @DisplayName("returns the same instance as the object is immutable")
            void ok() {
                var someValue = BigDecimal.valueOf(43);
                var result = testInstance.apply(BigDecimal.class, someValue);
                assertSame(someValue, result);
            }
        }

    }

    @Nested
    @DisplayName("supports()")
    class SupportsTest {
        @Test
        @DisplayName("both classes are safe but not the same")
        void safeButNotSame() {
            assertFalse(testInstance.supports(LocalTime.class, LocalDateTime.class));
        }

        @Test
        @DisplayName("both classes are not safe")
        void bothAreNotSafe() {
            assertFalse(testInstance.supports(Object.class, Object.class));
        }

        @Test
        @DisplayName("source is unsafe")
        void sourceIsUnsafe() {
            assertFalse(testInstance.supports(Object.class, Boolean.class));
        }

        @Test
        @DisplayName("target is unsafe")
        void targetIsUnsafe() {
            assertFalse(testInstance.supports(Boolean.class, Object.class));
        }

    }

    @Nested
    @DisplayName("apply()")
    class ApplyTest {

        private static final Instant someNow = Instant.now();
        private static final BigDecimal someValue = BigDecimal.valueOf(1234);

        class Source {
            Instant instant = someNow;
            BigDecimal value = someValue;
        }

        class Target {
            Instant instant;
            BigDecimal value;
        }

        class NotMatchingTarget {
            BigDecimal instant;
            Instant value;
        }

        @Test
        @DisplayName("copies enum fields from source to target")
        void ok() {
            var source = new Source();
            var target = new Target();

            JavaCopy.copyFields(source, target, Set.of(testInstance));

            assertSourceHasNotChanged(source);
            assertTargetHasChanged(target);
        }

        @Test
        @DisplayName("copies enum fields only if type matches")
        void typesAreNotMatching() {
            var source = new Source();
            var target = new NotMatchingTarget();

            JavaCopy.copyFields(source, target, Set.of(testInstance));

            assertSourceHasNotChanged(source);
            assertTargetHasNotChanged(target);
        }

        private void assertSourceHasNotChanged(Source source) {
            assertThat(source.instant, is(someNow));
            assertThat(source.value, is(someValue));
        }

        private void assertTargetHasChanged(Target target) {
            assertThat(target.instant, is(someNow));
            assertThat(target.value, is(someValue));
        }

        private void assertTargetHasNotChanged(NotMatchingTarget target) {
            assertNull(target.instant);
            assertNull(target.value);
        }
    }
}