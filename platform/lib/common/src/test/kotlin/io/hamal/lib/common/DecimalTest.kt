package io.hamal.lib.common

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest.dynamicTest

class DecimalTest {

    @Nested
    inner class InvokeTest {

        @Test
        fun `Byte input`() {
            val result = Decimal(28.toByte())
            assertThat(result, equalTo(expected))
        }

        @Test
        fun `Short input`() {
            val result = Decimal(28.toShort())
            assertThat(result, equalTo(expected))
        }

        @Test
        fun `Int input`() {
            val resul = Decimal(28)
            assertThat(resul, equalTo(expected))
        }

        @Test
        fun `Long input`() {
            val result = Decimal(28)
            assertThat(result, equalTo(expected))
        }

        @Test
        fun `Float input`() {
            val result = Decimal(28)
            assertThat(result, equalTo(expected))
        }

        @Test
        fun `Float nan input`() {
            val exception = assertThrows<IllegalArgumentException> {
                Decimal(Float.NaN)
            }
            assertThat(exception.message, containsString("NaN"))
        }

        @Test
        fun `Float positive infinity input`() {
            val exception = assertThrows<IllegalArgumentException> {
                Decimal(Float.POSITIVE_INFINITY)
            }
            assertThat(exception.message, containsString("Infinity"))
        }

        @Test
        fun `Float negative infinity input`() {
            val exception = assertThrows<IllegalArgumentException> {
                Decimal(Float.NEGATIVE_INFINITY)
            }
            assertThat(exception.message, containsString("Infinity"))
        }

        @Test
        fun `Double input`() {
            val result = Decimal(28)
            assertThat(result, equalTo(expected))
        }

        @Test
        fun `Double nan input`() {
            val exception = assertThrows<IllegalArgumentException> {
                Decimal(Double.NaN)
            }
            assertThat(exception.message, containsString("NaN"))
        }

        @Test
        fun `Double positive infinity input`() {
            val exception = assertThrows<IllegalArgumentException> {
                Decimal(Double.POSITIVE_INFINITY)
            }
            assertThat(exception.message, containsString("Infinity"))
        }

        @Test
        fun `Double negative infinity input`() {
            val exception = assertThrows<IllegalArgumentException> {
                Decimal(Double.NEGATIVE_INFINITY)
            }
            assertThat(exception.message, containsString("Infinity"))
        }

        @Test
        fun `String input`() {
            val result = Decimal("28")
            assertThat(result, equalTo(expected))
        }

        @Test
        fun `String nan input`() {
            val exception = assertThrows<IllegalArgumentException> {
                Decimal("NaN")
            }
            assertThat(exception.message, containsString("NaN"))
        }

        @Test
        fun `String empty input`() {
            val exception = assertThrows<IllegalArgumentException> {
                Decimal("")
            }
            assertThat(exception.message, containsString("NaN"))
        }

        @Test
        fun `String whitespaces input`() {
            val exception = assertThrows<IllegalArgumentException> {
                Decimal("    ")
            }
            assertThat(exception.message, containsString("NaN"))
        }

        private val expected = Decimal(28)
    }

    @Nested
    inner class PlusTest {
        @Test
        fun ok() {
            val testInstance = Decimal(28)
            val otherInstance = Decimal(10)

            val result = testInstance.plus(otherInstance)
            assertThat(result, equalTo(Decimal(38)))

            assertThat(testInstance, equalTo(Decimal(28)))
            assertThat(otherInstance, equalTo(Decimal(10)))
        }
    }

    @Nested
    inner class MinusTest {
        @Test
        fun ok() {
            val testInstance = Decimal(28)
            val otherInstance = Decimal(10)

            val result = testInstance.minus(otherInstance)
            assertThat(result, equalTo(Decimal(18)))

            assertThat(testInstance, equalTo(Decimal(28)))
            assertThat(otherInstance, equalTo(Decimal(10)))
        }
    }

    @Nested
    inner class MultiplyTest {
        @Test
        fun ok() {
            val testInstance = Decimal(28)
            val otherInstance = Decimal(10)

            val result = testInstance.multiply(otherInstance)
            assertThat(result, equalTo(Decimal(280)))

            assertThat(testInstance, equalTo(Decimal(28)))
            assertThat(otherInstance, equalTo(Decimal(10)))
        }
    }

    @Nested
    inner class DivideTest {
        @Test
        fun ok() {
            val testInstance = Decimal(280)
            val otherInstance = Decimal(10)

            val result = testInstance.divide(otherInstance)
            assertThat(result, equalTo(Decimal(28)))

            assertThat(testInstance, equalTo(Decimal(280)))
            assertThat(otherInstance, equalTo(Decimal(10)))
        }
    }

    @Nested
    inner class RemainderTest {
        @Test
        fun ok() {
            val testInstance = Decimal("42.0021224")
            val divisor = Decimal("3.14152")
            val result = testInstance.remainder(divisor)

            assertThat(result, equalTo(Decimal("1.1623624")))

            assertThat(testInstance, equalTo(Decimal("42.0021224")))
            assertThat(divisor, equalTo(Decimal("3.14152")))
        }
    }

    @Nested
    inner class PowTest {
        @Test
        fun ok() {
            val testInstance = Decimal("10")
            val exponent = Decimal("2")
            val result = testInstance.pow(exponent)

            assertThat(result, equalTo(Decimal("100")))

            assertThat(testInstance, equalTo(Decimal("10")))
            assertThat(exponent, equalTo(Decimal("2")))
        }
    }

    @Nested
    inner class NegateTest {
        @Test
        fun `Negative value`() {
            val testInstance = Decimal(-28)

            val result = testInstance.negate()
            assertThat(result, equalTo(Decimal(28)))

            assertThat(testInstance, equalTo(Decimal(-28)))
        }

        @Test
        fun `Positive value`() {
            val testInstance = Decimal(28)

            val result = testInstance.negate()
            assertThat(result, equalTo(Decimal(-28)))

            assertThat(testInstance, equalTo(Decimal(28)))
        }
    }

    @Nested
    inner class AbsTest {
        @Test
        fun `Negative value`() {
            val testInstance = Decimal(-28)

            val result = testInstance.abs()
            assertThat(result, equalTo(Decimal(28)))

            assertThat(testInstance, equalTo(Decimal(-28)))
        }

        @Test
        fun `Positive value`() {
            val testInstance = Decimal(28)

            val result = testInstance.abs()
            assertThat(result, equalTo(Decimal(28)))

            assertThat(testInstance, equalTo(Decimal(28)))
        }
    }

    @TestFactory
    fun floor() = listOf(
        Decimal(0) to Decimal("0"),
        Decimal("3.14152") to Decimal("3"),
        Decimal("42.999999999") to Decimal("42")
    ).map { (testInstance, expected) ->
        dynamicTest("floor of $testInstance") {
            val result = testInstance.floor()
            assertThat(result, equalTo(expected))
        }
    }

    @TestFactory
    fun ceil() = listOf(
        Decimal(0) to Decimal("0"),
        Decimal("3.14152") to Decimal("4"),
        Decimal("42.999999999") to Decimal("43")
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
            val testInstance = Decimal(-42)
            val exception = assertThrows<IllegalArgumentException> {
                testInstance.ln()
            }
            assertThat(exception.message, containsString("Value must >= 1"))
        }

        @Test
        fun `Value is 0`() {
            val testInstance = Decimal(0)
            val exception = assertThrows<IllegalArgumentException> {
                testInstance.ln()
            }
            assertThat(exception.message, containsString("Value must >= 1"))
        }

        @Test

        fun `Value is 1`() {
            val testInstance = Decimal(1)

            val result = testInstance.ln()
            assertThat(result, equalTo(Decimal(0)))

            assertThat(testInstance, equalTo(Decimal(1)))
        }

        @TestFactory
        fun test() = listOf(
            Decimal(2) to Decimal("0.6931471805599453094172321214581766"),
            Decimal("1234") to Decimal("7.118016204465333123414803800068370"),
            Decimal("1234.09901234123") to Decimal("7.118096438151897579984386821678212"),
            Decimal("987654") to Decimal("13.80308771296566413828433949825021"),
            Decimal("9876543") to Decimal("16.10567236153744431962426738978479"),
            Decimal("100000") to Decimal("11.51292546497022842008995727342182"),
            Decimal("1000000") to Decimal("13.81551055796427410410675612270492")
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
            val testInstance = Decimal(-42)
            val exception = assertThrows<IllegalStateException> {
                testInstance.sqrt()
            }
            assertThat(exception.message, containsString("Value must >= 0"))
        }

        @TestFactory
        fun sqrt() = listOf(
            Decimal(0) to Decimal("0"),
            Decimal(1) to Decimal("1"),
            Decimal(2) to Decimal("1.414213562373095048801688724209698"),
            Decimal("1234.09901234123") to Decimal("35.12974540672377261172508927243486"),
            Decimal("987654") to Decimal("993.8078285060950353793489213512089"),
            Decimal("9876543") to Decimal("3142.696771882390819470725583679575"),
            Decimal("100000") to Decimal("316.2277660168379331998893544432719"),
            Decimal("1000000") to Decimal("1000")
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
            val testInstance = Decimal(5)
            val otherInstance = Decimal(10)
            assertTrue(testInstance.isLessThan(otherInstance))
        }

        @Test
        fun `Equal to`() {
            val testInstance = Decimal(5)
            val otherInstance = Decimal(5)
            assertFalse(testInstance.isLessThan(otherInstance))
        }

        @Test
        fun `Greater Than`() {
            val testInstance = Decimal(10)
            val otherInstance = Decimal(5)
            assertFalse(testInstance.isLessThan(otherInstance))
        }
    }

    @Nested
    inner class IsLessThanEqualTest {
        @Test
        fun `Less Than`() {
            val testInstance = Decimal(5)
            val otherInstance = Decimal(10)
            assertTrue(testInstance.isLessThanEqual(otherInstance))
        }

        @Test
        fun `Equal to`() {
            val testInstance = Decimal(5)
            val otherInstance = Decimal(5)
            assertTrue(testInstance.isLessThanEqual(otherInstance))
        }

        @Test
        fun `Greater Than`() {
            val testInstance = Decimal(10)
            val otherInstance = Decimal(5)
            assertFalse(testInstance.isLessThanEqual(otherInstance))
        }
    }

    @Nested
    inner class IsGreaterThanTest {
        @Test
        fun `Less Than`() {
            val testInstance = Decimal(5)
            val otherInstance = Decimal(10)
            assertFalse(testInstance.isGreaterThan(otherInstance))
        }

        @Test
        fun `Equal to`() {
            val testInstance = Decimal(5)
            val otherInstance = Decimal(5)
            assertFalse(testInstance.isGreaterThan(otherInstance))
        }

        @Test
        fun `Greater Than`() {
            val testInstance = Decimal(10)
            val otherInstance = Decimal(5)
            assertTrue(testInstance.isGreaterThan(otherInstance))
        }
    }

    @Nested
    inner class IsGreaterThanEqualTest {
        @Test
        fun `Less Than`() {
            val testInstance = Decimal(5)
            val otherInstance = Decimal(10)
            assertFalse(testInstance.isGreaterThanEqual(otherInstance))
        }

        @Test
        fun `Equal to`() {
            val testInstance = Decimal(5)
            val otherInstance = Decimal(5)
            assertTrue(testInstance.isGreaterThanEqual(otherInstance))
        }

        @Test
        fun `Greater Than`() {
            val testInstance = Decimal(10)
            val otherInstance = Decimal(5)
            assertTrue(testInstance.isGreaterThanEqual(otherInstance))
        }
    }

    @Nested
    inner class IsNegativeTest {
        @Test
        fun `Negative number`() {
            val testInstance = Decimal(-10)
            assertTrue(testInstance.isNegative())
        }

        @Test
        fun `Zero`() {
            val testInstance = Decimal.Zero
            assertFalse(testInstance.isNegative())
        }

        @Test
        fun `Positive number`() {
            val testInstance = Decimal(10)
            assertFalse(testInstance.isNegative())
        }
    }

    @Nested
    inner class IsPositiveTest {
        @Test
        fun `Negative number`() {
            val testInstance = Decimal(-10)
            assertFalse(testInstance.isPositive())
        }

        @Test
        fun `Zero`() {
            val testInstance = Decimal.Zero
            assertFalse(testInstance.isPositive())
        }

        @Test
        fun `Positive number`() {
            val testInstance = Decimal(10)
            assertTrue(testInstance.isPositive())
        }
    }

    @Nested
    inner class IsZeroTest {
        @Test
        fun `Negative number`() {
            val testInstance = Decimal(-10)
            assertFalse(testInstance.isZero())
        }

        @Test
        fun `Zero`() {
            val testInstance = Decimal.Zero
            assertTrue(testInstance.isZero())
        }

        @Test
        fun `Positive number`() {
            val testInstance = Decimal(10)
            assertFalse(testInstance.isZero())
        }
    }

    @Nested
    inner class ToByteTest {
        @Test
        fun ok() {
            val testInstance = Decimal(28)
            assertThat(testInstance.toByte(), equalTo(28))
        }
    }

    @Nested
    inner class ToShortTest {
        @Test
        fun ok() {
            val testInstance = Decimal(28)
            assertThat(testInstance.toShort(), equalTo(28))
        }
    }

    @Nested
    inner class ToIntTest {
        @Test
        fun ok() {
            val testInstance = Decimal(28)
            assertThat(testInstance.toInt(), equalTo(28))
        }
    }

    @Nested
    inner class ToLongTest {
        @Test
        fun ok() {
            val testInstance = Decimal(28)
            assertThat(testInstance.toLong(), equalTo(28))
        }
    }

    @Nested
    inner class ToFloatTest {
        @Test
        fun ok() {
            val testInstance = Decimal(28.10)
            assertThat(testInstance.toFloat(), equalTo(28.10f))
        }
    }

    @Nested
    inner class ToDoubleTest {
        @Test
        fun ok() {
            val testInstance = Decimal(28.10)
            assertThat(testInstance.toDouble(), equalTo(28.10))
        }
    }

    @Nested
    inner class EqualsTest {

        @Test
        fun `Same value`() {
            val testInstance = Decimal(23)
            assertTrue(testInstance == Decimal(23))
        }

        @Test
        fun `Same value different representation`() {
            val testInstance = Decimal(23)
            assertTrue(testInstance == Decimal(23.00f))
        }

    }
}
