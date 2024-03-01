package io.hamal.lib.kua.type

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest.dynamicTest

class KuaDecimalTest {

    @Nested
    inner class InvokeTest {

        @Test
        fun `Byte input`() {
            val result = KuaDecimal(28.toByte())
            assertThat(result, equalTo(expected))
        }

        @Test
        fun `Short input`() {
            val result = KuaDecimal(28.toShort())
            assertThat(result, equalTo(expected))
        }

        @Test
        fun `Int input`() {
            val resul = KuaDecimal(28)
            assertThat(resul, equalTo(expected))
        }

        @Test
        fun `Long input`() {
            val result = KuaDecimal(28)
            assertThat(result, equalTo(expected))
        }

        @Test
        fun `Float input`() {
            val result = KuaDecimal(28)
            assertThat(result, equalTo(expected))
        }

        @Test
        fun `Float nan input`() {
            val exception = assertThrows<IllegalArgumentException> {
                KuaDecimal(Float.NaN)
            }
            assertThat(exception.message, containsString("NaN"))
        }

        @Test
        fun `Float positive infinity input`() {
            val exception = assertThrows<IllegalArgumentException> {
                KuaDecimal(Float.POSITIVE_INFINITY)
            }
            assertThat(exception.message, containsString("Infinity"))
        }

        @Test
        fun `Float negative infinity input`() {
            val exception = assertThrows<IllegalArgumentException> {
                KuaDecimal(Float.NEGATIVE_INFINITY)
            }
            assertThat(exception.message, containsString("Infinity"))
        }

        @Test
        fun `Double input`() {
            val result = KuaDecimal(28)
            assertThat(result, equalTo(expected))
        }

        @Test
        fun `Double nan input`() {
            val exception = assertThrows<IllegalArgumentException> {
                KuaDecimal(Double.NaN)
            }
            assertThat(exception.message, containsString("NaN"))
        }

        @Test
        fun `Double positive infinity input`() {
            val exception = assertThrows<IllegalArgumentException> {
                KuaDecimal(Double.POSITIVE_INFINITY)
            }
            assertThat(exception.message, containsString("Infinity"))
        }

        @Test
        fun `Double negative infinity input`() {
            val exception = assertThrows<IllegalArgumentException> {
                KuaDecimal(Double.NEGATIVE_INFINITY)
            }
            assertThat(exception.message, containsString("Infinity"))
        }

        @Test
        fun `String input`() {
            val result = KuaDecimal("28")
            assertThat(result, equalTo(expected))
        }

        @Test
        fun `String nan input`() {
            val exception = assertThrows<IllegalArgumentException> {
                KuaDecimal("NaN")
            }
            assertThat(exception.message, containsString("NaN"))
        }

        @Test
        fun `String empty input`() {
            val exception = assertThrows<IllegalArgumentException> {
                KuaDecimal("")
            }
            assertThat(exception.message, containsString("NaN"))
        }

        @Test
        fun `String whitespaces input`() {
            val exception = assertThrows<IllegalArgumentException> {
                KuaDecimal("    ")
            }
            assertThat(exception.message, containsString("NaN"))
        }

        private val expected = KuaDecimal(28)
    }

    @Nested
    inner class PlusTest {
        @Test
        fun ok() {
            val testInstance = KuaDecimal(28)
            val otherInstance = KuaDecimal(10)

            val result = testInstance.plus(otherInstance)
            assertThat(result, equalTo(KuaDecimal(38)))

            assertThat(testInstance, equalTo(KuaDecimal(28)))
            assertThat(otherInstance, equalTo(KuaDecimal(10)))
        }
    }

    @Nested
    inner class MinusTest {
        @Test
        fun ok() {
            val testInstance = KuaDecimal(28)
            val otherInstance = KuaDecimal(10)

            val result = testInstance.minus(otherInstance)
            assertThat(result, equalTo(KuaDecimal(18)))

            assertThat(testInstance, equalTo(KuaDecimal(28)))
            assertThat(otherInstance, equalTo(KuaDecimal(10)))
        }
    }

    @Nested
    inner class MultiplyTest {
        @Test
        fun ok() {
            val testInstance = KuaDecimal(28)
            val otherInstance = KuaDecimal(10)

            val result = testInstance.multiply(otherInstance)
            assertThat(result, equalTo(KuaDecimal(280)))

            assertThat(testInstance, equalTo(KuaDecimal(28)))
            assertThat(otherInstance, equalTo(KuaDecimal(10)))
        }
    }

    @Nested
    inner class DivideTest {
        @Test
        fun ok() {
            val testInstance = KuaDecimal(280)
            val otherInstance = KuaDecimal(10)

            val result = testInstance.divide(otherInstance)
            assertThat(result, equalTo(KuaDecimal(28)))

            assertThat(testInstance, equalTo(KuaDecimal(280)))
            assertThat(otherInstance, equalTo(KuaDecimal(10)))
        }
    }

    @Nested
    inner class RemainderTest {
        @Test
        fun ok() {
            val testInstance = KuaDecimal("42.0021224")
            val divisor = KuaDecimal("3.14152")
            val result = testInstance.remainder(divisor)

            assertThat(result, equalTo(KuaDecimal("1.1623624")))

            assertThat(testInstance, equalTo(KuaDecimal("42.0021224")))
            assertThat(divisor, equalTo(KuaDecimal("3.14152")))
        }
    }

    @Nested
    inner class PowTest {
        @Test
        fun ok() {
            val testInstance = KuaDecimal("10")
            val exponent = KuaDecimal("2")
            val result = testInstance.pow(exponent)

            assertThat(result, equalTo(KuaDecimal("100")))

            assertThat(testInstance, equalTo(KuaDecimal("10")))
            assertThat(exponent, equalTo(KuaDecimal("2")))
        }
    }

    @Nested
    inner class NegateTest {
        @Test
        fun `Negative value`() {
            val testInstance = KuaDecimal(-28)

            val result = testInstance.negate()
            assertThat(result, equalTo(KuaDecimal(28)))

            assertThat(testInstance, equalTo(KuaDecimal(-28)))
        }

        @Test
        fun `Positive value`() {
            val testInstance = KuaDecimal(28)

            val result = testInstance.negate()
            assertThat(result, equalTo(KuaDecimal(-28)))

            assertThat(testInstance, equalTo(KuaDecimal(28)))
        }
    }

    @Nested
    inner class AbsTest {
        @Test
        fun `Negative value`() {
            val testInstance = KuaDecimal(-28)

            val result = testInstance.abs()
            assertThat(result, equalTo(KuaDecimal(28)))

            assertThat(testInstance, equalTo(KuaDecimal(-28)))
        }

        @Test
        fun `Positive value`() {
            val testInstance = KuaDecimal(28)

            val result = testInstance.abs()
            assertThat(result, equalTo(KuaDecimal(28)))

            assertThat(testInstance, equalTo(KuaDecimal(28)))
        }
    }

    @TestFactory
    fun floor() = listOf(
        KuaDecimal(0) to KuaDecimal("0"),
        KuaDecimal("3.14152") to KuaDecimal("3"),
        KuaDecimal("42.999999999") to KuaDecimal("42")
    ).map { (testInstance, expected) ->
        dynamicTest("floor of $testInstance") {
            val result = testInstance.floor()
            assertThat(result, equalTo(expected))
        }
    }

    @TestFactory
    fun ceil() = listOf(
        KuaDecimal(0) to KuaDecimal("0"),
        KuaDecimal("3.14152") to KuaDecimal("4"),
        KuaDecimal("42.999999999") to KuaDecimal("43")
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
            val testInstance = KuaDecimal(-42)
            val exception = assertThrows<IllegalArgumentException> {
                testInstance.ln()
            }
            assertThat(exception.message, containsString("Value must >= 1"))
        }

        @Test
        fun `Value is 0`() {
            val testInstance = KuaDecimal(0)
            val exception = assertThrows<IllegalArgumentException> {
                testInstance.ln()
            }
            assertThat(exception.message, containsString("Value must >= 1"))
        }

        @Test

        fun `Value is 1`() {
            val testInstance = KuaDecimal(1)

            val result = testInstance.ln()
            assertThat(result, equalTo(KuaDecimal(0)))

            assertThat(testInstance, equalTo(KuaDecimal(1)))
        }

        @TestFactory
        fun test() = listOf(
            KuaDecimal(2) to KuaDecimal("0.6931471805599453094172321214581766"),
            KuaDecimal("1234") to KuaDecimal("7.118016204465333123414803800068370"),
            KuaDecimal("1234.09901234123") to KuaDecimal("7.118096438151897579984386821678212"),
            KuaDecimal("987654") to KuaDecimal("13.80308771296566413828433949825021"),
            KuaDecimal("9876543") to KuaDecimal("16.10567236153744431962426738978479"),
            KuaDecimal("100000") to KuaDecimal("11.51292546497022842008995727342182"),
            KuaDecimal("1000000") to KuaDecimal("13.81551055796427410410675612270492")
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
            val testInstance = KuaDecimal(-42)
            val exception = assertThrows<IllegalStateException> {
                testInstance.sqrt()
            }
            assertThat(exception.message, containsString("Value must >= 0"))
        }

        @TestFactory
        fun sqrt() = listOf(
            KuaDecimal(0) to KuaDecimal("0"),
            KuaDecimal(1) to KuaDecimal("1"),
            KuaDecimal(2) to KuaDecimal("1.414213562373095048801688724209698"),
            KuaDecimal("1234.09901234123") to KuaDecimal("35.12974540672377261172508927243486"),
            KuaDecimal("987654") to KuaDecimal("993.8078285060950353793489213512089"),
            KuaDecimal("9876543") to KuaDecimal("3142.696771882390819470725583679575"),
            KuaDecimal("100000") to KuaDecimal("316.2277660168379331998893544432719"),
            KuaDecimal("1000000") to KuaDecimal("1000")
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
            val testInstance = KuaDecimal(5)
            val otherInstance = KuaDecimal(10)
            assertTrue(testInstance.isLessThan(otherInstance))
        }

        @Test
        fun `Equal to`() {
            val testInstance = KuaDecimal(5)
            val otherInstance = KuaDecimal(5)
            assertFalse(testInstance.isLessThan(otherInstance))
        }

        @Test
        fun `Greater Than`() {
            val testInstance = KuaDecimal(10)
            val otherInstance = KuaDecimal(5)
            assertFalse(testInstance.isLessThan(otherInstance))
        }
    }

    @Nested
    inner class IsLessThanEqualTest {
        @Test
        fun `Less Than`() {
            val testInstance = KuaDecimal(5)
            val otherInstance = KuaDecimal(10)
            assertTrue(testInstance.isLessThanEqual(otherInstance))
        }

        @Test
        fun `Equal to`() {
            val testInstance = KuaDecimal(5)
            val otherInstance = KuaDecimal(5)
            assertTrue(testInstance.isLessThanEqual(otherInstance))
        }

        @Test
        fun `Greater Than`() {
            val testInstance = KuaDecimal(10)
            val otherInstance = KuaDecimal(5)
            assertFalse(testInstance.isLessThanEqual(otherInstance))
        }
    }

    @Nested
    inner class IsGreaterThanTest {
        @Test
        fun `Less Than`() {
            val testInstance = KuaDecimal(5)
            val otherInstance = KuaDecimal(10)
            assertFalse(testInstance.isGreaterThan(otherInstance))
        }

        @Test
        fun `Equal to`() {
            val testInstance = KuaDecimal(5)
            val otherInstance = KuaDecimal(5)
            assertFalse(testInstance.isGreaterThan(otherInstance))
        }

        @Test
        fun `Greater Than`() {
            val testInstance = KuaDecimal(10)
            val otherInstance = KuaDecimal(5)
            assertTrue(testInstance.isGreaterThan(otherInstance))
        }
    }

    @Nested
    inner class IsGreaterThanEqualTest {
        @Test
        fun `Less Than`() {
            val testInstance = KuaDecimal(5)
            val otherInstance = KuaDecimal(10)
            assertFalse(testInstance.isGreaterThanEqual(otherInstance))
        }

        @Test
        fun `Equal to`() {
            val testInstance = KuaDecimal(5)
            val otherInstance = KuaDecimal(5)
            assertTrue(testInstance.isGreaterThanEqual(otherInstance))
        }

        @Test
        fun `Greater Than`() {
            val testInstance = KuaDecimal(10)
            val otherInstance = KuaDecimal(5)
            assertTrue(testInstance.isGreaterThanEqual(otherInstance))
        }
    }

    @Nested
    inner class IsNegativeTest {
        @Test
        fun `Negative number`() {
            val testInstance = KuaDecimal(-10)
            assertTrue(testInstance.isNegative())
        }

        @Test
        fun `Zero`() {
            val testInstance = KuaDecimal.Zero
            assertFalse(testInstance.isNegative())
        }

        @Test
        fun `Positive number`() {
            val testInstance = KuaDecimal(10)
            assertFalse(testInstance.isNegative())
        }
    }

    @Nested
    inner class IsPositiveTest {
        @Test
        fun `Negative number`() {
            val testInstance = KuaDecimal(-10)
            assertFalse(testInstance.isPositive())
        }

        @Test
        fun `Zero`() {
            val testInstance = KuaDecimal.Zero
            assertFalse(testInstance.isPositive())
        }

        @Test
        fun `Positive number`() {
            val testInstance = KuaDecimal(10)
            assertTrue(testInstance.isPositive())
        }
    }

    @Nested
    inner class IsZeroTest {
        @Test
        fun `Negative number`() {
            val testInstance = KuaDecimal(-10)
            assertFalse(testInstance.isZero())
        }

        @Test
        fun `Zero`() {
            val testInstance = KuaDecimal.Zero
            assertTrue(testInstance.isZero())
        }

        @Test
        fun `Positive number`() {
            val testInstance = KuaDecimal(10)
            assertFalse(testInstance.isZero())
        }
    }

    @Nested
    inner class ToByteTest {
        @Test
        fun ok() {
            val testInstance = KuaDecimal(28)
            assertThat(testInstance.toByte(), equalTo(28))
        }
    }

    @Nested
    inner class ToShortTest {
        @Test
        fun ok() {
            val testInstance = KuaDecimal(28)
            assertThat(testInstance.toShort(), equalTo(28))
        }
    }

    @Nested
    inner class ToIntTest {
        @Test
        fun ok() {
            val testInstance = KuaDecimal(28)
            assertThat(testInstance.toInt(), equalTo(28))
        }
    }

    @Nested
    inner class ToLongTest {
        @Test
        fun ok() {
            val testInstance = KuaDecimal(28)
            assertThat(testInstance.toLong(), equalTo(28))
        }
    }

    @Nested
    inner class ToFloatTest {
        @Test
        fun ok() {
            val testInstance = KuaDecimal(28.10)
            assertThat(testInstance.toFloat(), equalTo(28.10f))
        }
    }

    @Nested
    inner class ToDoubleTest {
        @Test
        fun ok() {
            val testInstance = KuaDecimal(28.10)
            assertThat(testInstance.toDouble(), equalTo(28.10))
        }
    }

}
