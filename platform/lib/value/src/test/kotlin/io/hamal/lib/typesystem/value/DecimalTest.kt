package io.hamal.lib.typesystem.value

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest.dynamicTest

class ValueDecimalTest {

    @Nested
    inner class InvokeTest {

        @Test
        fun `Byte input`() {
            val result = ValueDecimal(28.toByte())
            assertThat(result, equalTo(expected))
        }

        @Test
        fun `Short input`() {
            val result = ValueDecimal(28.toShort())
            assertThat(result, equalTo(expected))
        }

        @Test
        fun `Int input`() {
            val resul = ValueDecimal(28)
            assertThat(resul, equalTo(expected))
        }

        @Test
        fun `Long input`() {
            val result = ValueDecimal(28)
            assertThat(result, equalTo(expected))
        }

        @Test
        fun `Float input`() {
            val result = ValueDecimal(28)
            assertThat(result, equalTo(expected))
        }

        @Test
        fun `Float nan input`() {
            val exception = assertThrows<IllegalArgumentException> {
                ValueDecimal(Float.NaN)
            }
            assertThat(exception.message, containsString("NaN"))
        }

        @Test
        fun `Float positive infinity input`() {
            val exception = assertThrows<IllegalArgumentException> {
                ValueDecimal(Float.POSITIVE_INFINITY)
            }
            assertThat(exception.message, containsString("Infinity"))
        }

        @Test
        fun `Float negative infinity input`() {
            val exception = assertThrows<IllegalArgumentException> {
                ValueDecimal(Float.NEGATIVE_INFINITY)
            }
            assertThat(exception.message, containsString("Infinity"))
        }

        @Test
        fun `Double input`() {
            val result = ValueDecimal(28)
            assertThat(result, equalTo(expected))
        }

        @Test
        fun `Double nan input`() {
            val exception = assertThrows<IllegalArgumentException> {
                ValueDecimal(Double.NaN)
            }
            assertThat(exception.message, containsString("NaN"))
        }

        @Test
        fun `Double positive infinity input`() {
            val exception = assertThrows<IllegalArgumentException> {
                ValueDecimal(Double.POSITIVE_INFINITY)
            }
            assertThat(exception.message, containsString("Infinity"))
        }

        @Test
        fun `Double negative infinity input`() {
            val exception = assertThrows<IllegalArgumentException> {
                ValueDecimal(Double.NEGATIVE_INFINITY)
            }
            assertThat(exception.message, containsString("Infinity"))
        }

        @Test
        fun `String input`() {
            val result = ValueDecimal("28")
            assertThat(result, equalTo(expected))
        }

        @Test
        fun `String nan input`() {
            val exception = assertThrows<IllegalArgumentException> {
                ValueDecimal("NaN")
            }
            assertThat(exception.message, containsString("NaN"))
        }

        @Test
        fun `String empty input`() {
            val exception = assertThrows<IllegalArgumentException> {
                ValueDecimal("")
            }
            assertThat(exception.message, containsString("NaN"))
        }

        @Test
        fun `String whitespaces input`() {
            val exception = assertThrows<IllegalArgumentException> {
                ValueDecimal("    ")
            }
            assertThat(exception.message, containsString("NaN"))
        }

        private val expected = ValueDecimal(28)
    }

    @Nested
    inner class PlusTest {
        @Test
        fun ok() {
            val testInstance = ValueDecimal(28)
            val otherInstance = ValueDecimal(10)

            val result = testInstance.plus(otherInstance)
            assertThat(result, equalTo(ValueDecimal(38)))

            assertThat(testInstance, equalTo(ValueDecimal(28)))
            assertThat(otherInstance, equalTo(ValueDecimal(10)))
        }
    }

    @Nested
    inner class MinusTest {
        @Test
        fun ok() {
            val testInstance = ValueDecimal(28)
            val otherInstance = ValueDecimal(10)

            val result = testInstance.minus(otherInstance)
            assertThat(result, equalTo(ValueDecimal(18)))

            assertThat(testInstance, equalTo(ValueDecimal(28)))
            assertThat(otherInstance, equalTo(ValueDecimal(10)))
        }
    }

    @Nested
    inner class MultiplyTest {
        @Test
        fun ok() {
            val testInstance = ValueDecimal(28)
            val otherInstance = ValueDecimal(10)

            val result = testInstance.multiply(otherInstance)
            assertThat(result, equalTo(ValueDecimal(280)))

            assertThat(testInstance, equalTo(ValueDecimal(28)))
            assertThat(otherInstance, equalTo(ValueDecimal(10)))
        }
    }

    @Nested
    inner class DivideTest {
        @Test
        fun ok() {
            val testInstance = ValueDecimal(280)
            val otherInstance = ValueDecimal(10)

            val result = testInstance.divide(otherInstance)
            assertThat(result, equalTo(ValueDecimal(28)))

            assertThat(testInstance, equalTo(ValueDecimal(280)))
            assertThat(otherInstance, equalTo(ValueDecimal(10)))
        }
    }

    @Nested
    inner class RemainderTest {
        @Test
        fun ok() {
            val testInstance = ValueDecimal("42.0021224")
            val divisor = ValueDecimal("3.14152")
            val result = testInstance.remainder(divisor)

            assertThat(result, equalTo(ValueDecimal("1.1623624")))

            assertThat(testInstance, equalTo(ValueDecimal("42.0021224")))
            assertThat(divisor, equalTo(ValueDecimal("3.14152")))
        }
    }

    @Nested
    inner class PowTest {
        @Test
        fun ok() {
            val testInstance = ValueDecimal("10")
            val exponent = ValueDecimal("2")
            val result = testInstance.pow(exponent)

            assertThat(result, equalTo(ValueDecimal("100")))

            assertThat(testInstance, equalTo(ValueDecimal("10")))
            assertThat(exponent, equalTo(ValueDecimal("2")))
        }
    }

    @Nested
    inner class NegateTest {
        @Test
        fun `Negative value`() {
            val testInstance = ValueDecimal(-28)

            val result = testInstance.negate()
            assertThat(result, equalTo(ValueDecimal(28)))

            assertThat(testInstance, equalTo(ValueDecimal(-28)))
        }

        @Test
        fun `Positive value`() {
            val testInstance = ValueDecimal(28)

            val result = testInstance.negate()
            assertThat(result, equalTo(ValueDecimal(-28)))

            assertThat(testInstance, equalTo(ValueDecimal(28)))
        }
    }

    @Nested
    inner class AbsTest {
        @Test
        fun `Negative value`() {
            val testInstance = ValueDecimal(-28)

            val result = testInstance.abs()
            assertThat(result, equalTo(ValueDecimal(28)))

            assertThat(testInstance, equalTo(ValueDecimal(-28)))
        }

        @Test
        fun `Positive value`() {
            val testInstance = ValueDecimal(28)

            val result = testInstance.abs()
            assertThat(result, equalTo(ValueDecimal(28)))

            assertThat(testInstance, equalTo(ValueDecimal(28)))
        }
    }

    @TestFactory
    fun floor() = listOf(
        ValueDecimal(0) to ValueDecimal("0"),
        ValueDecimal("3.14152") to ValueDecimal("3"),
        ValueDecimal("42.999999999") to ValueDecimal("42")
    ).map { (testInstance, expected) ->
        dynamicTest("floor of $testInstance") {
            val result = testInstance.floor()
            assertThat(result, equalTo(expected))
        }
    }

    @TestFactory
    fun ceil() = listOf(
        ValueDecimal(0) to ValueDecimal("0"),
        ValueDecimal("3.14152") to ValueDecimal("4"),
        ValueDecimal("42.999999999") to ValueDecimal("43")
    ).map { (testInstance, expected) ->
        dynamicTest("ceil of $testInstance") {
            val result = testInstance.ceil()
            assertThat(result, equalTo(expected))
        }
    }

    @Nested
    inner class LnTest {
        @Test
        fun `Value is negative`() {
            val testInstance = ValueDecimal(-42)
            val exception = assertThrows<IllegalArgumentException> {
                testInstance.ln()
            }
            assertThat(exception.message, containsString("Value must >= 1"))
        }

        @Test
        fun `Value is 0`() {
            val testInstance = ValueDecimal(0)
            val exception = assertThrows<IllegalArgumentException> {
                testInstance.ln()
            }
            assertThat(exception.message, containsString("Value must >= 1"))
        }

        @Test

        fun `Value is 1`() {
            val testInstance = ValueDecimal(1)

            val result = testInstance.ln()
            assertThat(result, equalTo(ValueDecimal(0)))

            assertThat(testInstance, equalTo(ValueDecimal(1)))
        }

        @TestFactory
        fun test() = listOf(
            ValueDecimal(2) to ValueDecimal("0.6931471805599453094172321214581766"),
            ValueDecimal("1234") to ValueDecimal("7.118016204465333123414803800068370"),
            ValueDecimal("1234.09901234123") to ValueDecimal("7.118096438151897579984386821678212"),
            ValueDecimal("987654") to ValueDecimal("13.80308771296566413828433949825021"),
            ValueDecimal("9876543") to ValueDecimal("16.10567236153744431962426738978479"),
            ValueDecimal("100000") to ValueDecimal("11.51292546497022842008995727342182"),
            ValueDecimal("1000000") to ValueDecimal("13.81551055796427410410675612270492")
        ).map { (testInstance, expected) ->
            dynamicTest("ln of $testInstance") {
                val result = testInstance.ln()
                assertThat(result, equalTo(expected))
            }
        }
    }

    @Nested

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class SqrtTest {

        @Test
        fun `Value is negative`() {
            val testInstance = ValueDecimal(-42)
            val exception = assertThrows<IllegalStateException> {
                testInstance.sqrt()
            }
            assertThat(exception.message, containsString("Value must >= 0"))
        }

        @TestFactory
        fun sqrt() = listOf(
            ValueDecimal(0) to ValueDecimal("0"),
            ValueDecimal(1) to ValueDecimal("1"),
            ValueDecimal(2) to ValueDecimal("1.414213562373095048801688724209698"),
            ValueDecimal("1234.09901234123") to ValueDecimal("35.12974540672377261172508927243486"),
            ValueDecimal("987654") to ValueDecimal("993.8078285060950353793489213512089"),
            ValueDecimal("9876543") to ValueDecimal("3142.696771882390819470725583679575"),
            ValueDecimal("100000") to ValueDecimal("316.2277660168379331998893544432719"),
            ValueDecimal("1000000") to ValueDecimal("1000")
        ).map { (testInstance, expected) ->
            dynamicTest("sqrt of $testInstance") {
                val result = testInstance.sqrt()
                assertThat(result, equalTo(expected))
            }
        }
    }

    @Nested
    inner class IsLessThanTest {
        @Test
        fun `Less Than`() {
            val testInstance = ValueDecimal(5)
            val otherInstance = ValueDecimal(10)
            assertTrue(testInstance.isLessThan(otherInstance))
        }

        @Test
        fun `Equal to`() {
            val testInstance = ValueDecimal(5)
            val otherInstance = ValueDecimal(5)
            assertFalse(testInstance.isLessThan(otherInstance))
        }

        @Test
        fun `Greater Than`() {
            val testInstance = ValueDecimal(10)
            val otherInstance = ValueDecimal(5)
            assertFalse(testInstance.isLessThan(otherInstance))
        }
    }

    @Nested
    inner class IsLessThanEqualTest {
        @Test
        fun `Less Than`() {
            val testInstance = ValueDecimal(5)
            val otherInstance = ValueDecimal(10)
            assertTrue(testInstance.isLessThanEqual(otherInstance))
        }

        @Test
        fun `Equal to`() {
            val testInstance = ValueDecimal(5)
            val otherInstance = ValueDecimal(5)
            assertTrue(testInstance.isLessThanEqual(otherInstance))
        }

        @Test
        fun `Greater Than`() {
            val testInstance = ValueDecimal(10)
            val otherInstance = ValueDecimal(5)
            assertFalse(testInstance.isLessThanEqual(otherInstance))
        }
    }

    @Nested
    inner class IsGreaterThanTest {
        @Test
        fun `Less Than`() {
            val testInstance = ValueDecimal(5)
            val otherInstance = ValueDecimal(10)
            assertFalse(testInstance.isGreaterThan(otherInstance))
        }

        @Test
        fun `Equal to`() {
            val testInstance = ValueDecimal(5)
            val otherInstance = ValueDecimal(5)
            assertFalse(testInstance.isGreaterThan(otherInstance))
        }

        @Test
        fun `Greater Than`() {
            val testInstance = ValueDecimal(10)
            val otherInstance = ValueDecimal(5)
            assertTrue(testInstance.isGreaterThan(otherInstance))
        }
    }

    @Nested
    inner class IsGreaterThanEqualTest {
        @Test
        fun `Less Than`() {
            val testInstance = ValueDecimal(5)
            val otherInstance = ValueDecimal(10)
            assertFalse(testInstance.isGreaterThanEqual(otherInstance))
        }

        @Test
        fun `Equal to`() {
            val testInstance = ValueDecimal(5)
            val otherInstance = ValueDecimal(5)
            assertTrue(testInstance.isGreaterThanEqual(otherInstance))
        }

        @Test
        fun `Greater Than`() {
            val testInstance = ValueDecimal(10)
            val otherInstance = ValueDecimal(5)
            assertTrue(testInstance.isGreaterThanEqual(otherInstance))
        }
    }

    @Nested
    inner class IsNegativeTest {
        @Test
        fun `Negative number`() {
            val testInstance = ValueDecimal(-10)
            assertTrue(testInstance.isNegative())
        }

        @Test
        fun `Zero`() {
            val testInstance = ValueDecimal.Zero
            assertFalse(testInstance.isNegative())
        }

        @Test
        fun `Positive number`() {
            val testInstance = ValueDecimal(10)
            assertFalse(testInstance.isNegative())
        }
    }

    @Nested
    inner class IsPositiveTest {
        @Test
        fun `Negative number`() {
            val testInstance = ValueDecimal(-10)
            assertFalse(testInstance.isPositive())
        }

        @Test
        fun `Zero`() {
            val testInstance = ValueDecimal.Zero
            assertFalse(testInstance.isPositive())
        }

        @Test
        fun `Positive number`() {
            val testInstance = ValueDecimal(10)
            assertTrue(testInstance.isPositive())
        }
    }

    @Nested
    inner class IsZeroTest {
        @Test
        fun `Negative number`() {
            val testInstance = ValueDecimal(-10)
            assertFalse(testInstance.isZero())
        }

        @Test
        fun `Zero`() {
            val testInstance = ValueDecimal.Zero
            assertTrue(testInstance.isZero())
        }

        @Test
        fun `Positive number`() {
            val testInstance = ValueDecimal(10)
            assertFalse(testInstance.isZero())
        }
    }

    @Nested
    inner class ToByteTest {
        @Test
        fun ok() {
            val testInstance = ValueDecimal(28)
            assertThat(testInstance.toByte(), equalTo(28))
        }
    }

    @Nested
    inner class ToShortTest {
        @Test
        fun ok() {
            val testInstance = ValueDecimal(28)
            assertThat(testInstance.toShort(), equalTo(28))
        }
    }

    @Nested
    inner class ToIntTest {
        @Test
        fun ok() {
            val testInstance = ValueDecimal(28)
            assertThat(testInstance.toInt(), equalTo(28))
        }
    }

    @Nested
    inner class ToLongTest {
        @Test
        fun ok() {
            val testInstance = ValueDecimal(28)
            assertThat(testInstance.toLong(), equalTo(28))
        }
    }

    @Nested
    inner class ToFloatTest {
        @Test
        fun ok() {
            val testInstance = ValueDecimal(28.10)
            assertThat(testInstance.toFloat(), equalTo(28.10f))
        }
    }

    @Nested
    inner class ToDoubleTest {
        @Test
        fun ok() {
            val testInstance = ValueDecimal(28.10)
            assertThat(testInstance.toDouble(), equalTo(28.10))
        }
    }

}
