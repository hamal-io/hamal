package io.hamal.lib.common.util.copy

import io.hamal.lib.common.ddd.ValueObject
import io.hamal.lib.common.util.ReflectionUtils
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@Nested

class CopyTest {
    @Nested
    inner class ImmutableClassesTest {
        @Test
        fun `There are 0 immutable classes declared`() {
            assertThat(Copy.immutableClasses, hasSize(0))
        }
    }

    @Nested
    inner class CopyTest {

        @Test
        fun `Copies values from BaseClass`() {
            val source = BaseClass(1, 2, 3, 4)
            val target = BaseClass(0, 0, 0, 0)
            Copy(source, target)

            assertThat(ReflectionUtils.getValue(target, "privateBaseValue"), equalTo(1))
            assertThat(ReflectionUtils.getValue(target, "protectedBaseValue"), equalTo(2))
            assertThat(ReflectionUtils.getValue(target, "internalBaseValue"), equalTo(3))
            assertThat(ReflectionUtils.getValue(target, "publicBaseValue"), equalTo(4))
        }

        @Test
        fun `Copies all values from DerivedClass`() {
            val source = DerivedClass(
                1, 2, 3, 4, 5, 6, 7, 8
            )

            val target = DerivedClass(
                0, 0, 0, 0,
                0, 0, 0, 0,
            )

            Copy(source, target)
            assertThat(ReflectionUtils.getValue(target, "privateBaseValue"), equalTo(1))
            assertThat(ReflectionUtils.getValue(target, "protectedBaseValue"), equalTo(2))
            assertThat(ReflectionUtils.getValue(target, "internalBaseValue"), equalTo(3))
            assertThat(ReflectionUtils.getValue(target, "publicBaseValue"), equalTo(4))
            assertThat(ReflectionUtils.getValue(target, "privateDerivedValue"), equalTo(5))
            assertThat(ReflectionUtils.getValue(target, "protectedDerivedValue"), equalTo(6))
            assertThat(ReflectionUtils.getValue(target, "internalDerivedValue"), equalTo(7))
            assertThat(ReflectionUtils.getValue(target, "publicDerivedValue"), equalTo(8))
        }

        @Test
        fun `Copies nullable values as well`() {
            data class SomeNullableClass(val x: Int? = null, val y: Int? = null)

            val source = SomeNullableClass(28, 10)
            val target = SomeNullableClass()
            Copy(source, target)
            assertThat(target.x, equalTo(28))
            assertThat(target.y, equalTo(10))
        }

        @Test
        fun `Copies null values as well`() {
            data class SomeNullableClass(val x: Int? = null)

            val source = SomeNullableClass(null)
            val target = SomeNullableClass(2810)
            Copy(source, target)
            assertThat(target.x, nullValue())
        }

        @Test
        fun `Copies from nullable to not nullable`() {
            data class SomeSource(val value: Int? = null)
            data class SomeTarget(val value: Int)

            val source = SomeSource(10)
            val target = SomeTarget(42)
            Copy(source, target)
            assertThat(target.value, equalTo(10))
        }

        @Test
        fun `Copies from not nullable to nullable`() {
            data class SomeSource(val value: Int)
            data class SomeTarget(val value: Int? = null)

            val source = SomeSource(10)
            var target = SomeTarget(42)
            Copy(source, target)
            assertThat(target.value, equalTo(10))

            target = SomeTarget(null)
            Copy(source, target)
            assertThat(target.value, equalTo(10))
        }

        @Test
        fun `Can not replace non nullable value with null value`() {
            data class SomeSource(val value: Int? = null)
            data class SomeTarget(val value: Int)

            val source = SomeSource(null)
            val target = SomeTarget(42)
            val exception = assertThrows<RuntimeException> {
                Copy(source, target)
            }
            assertThat(
                exception.cause?.message,
                equalTo("Can not set final int field io.hamal.lib.common.util.copy.CopyTest\$CopyTest\$Can not replace non nullable value with null value\$SomeTarget.value to null value")
            )
        }

        @Test
        fun `Copies ValueObject`() {
            data class SomeSource(val value: TestValueObject)
            data class SomeTarget(val value: TestValueObject?)

            val source = SomeSource(TestValueObject(10))
            val target = SomeTarget(null)
            Copy(source, target)
            assertThat(target.value, equalTo(TestValueObject(10)))
        }

        @Test
        fun `Copies non value object  to value object`() {
            data class SomeSource(val value: Int)
            data class SomeTarget(val value: TestValueObject?)

            val source = SomeSource(2810)
            val target = SomeTarget(null)
            Copy(source, target)
            assertThat(target.value, equalTo(TestValueObject(2810)))
        }

        @Test
        fun `Copies value object to non value object`() {
            data class SomeSource(val value: TestValueObject)
            data class SomeTarget(val value: Int?)

            val source = SomeSource(TestValueObject(1506))
            val target = SomeTarget(null)
            Copy(source, target)
            assertThat(target.value, equalTo(1506))
        }

        @Test
        fun `Copies enum values`() {
            data class SomeSource(val value: SomeEnum)
            data class SomeTarget(val value: SomeEnum)

            val source = SomeSource(SomeEnum.VALUE_2)
            val target = SomeTarget(SomeEnum.VALUE_3)
            Copy(source, target)
            assertThat(target, equalTo(SomeTarget(SomeEnum.VALUE_2)))
        }
    }

    open inner class BaseClass(
        private val privateBaseValue: Int,
        protected val protectedBaseValue: Int,
        internal val internalBaseValue: Int,
        val publicBaseValue: Int
    )

    open inner class DerivedClass(
        privateBaseValue: Int,
        protectedBaseValue: Int,
        internalBaseValue: Int,
        publicBaseValue: Int,
        private val privateDerivedValue: Int,
        private val protectedDerivedValue: Int,
        private val internalDerivedValue: Int,
        private val publicDerivedValue: Int
    ) : BaseClass(privateBaseValue, protectedBaseValue, internalBaseValue, publicBaseValue)

    class TestValueObject(override val value: Int) : ValueObject.ComparableImpl<Int>()

    enum class SomeEnum { VALUE_1, VALUE_2, VALUE_3 }
}
