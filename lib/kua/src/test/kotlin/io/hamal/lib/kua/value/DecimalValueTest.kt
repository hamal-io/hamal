package io.hamal.lib.kua.value

import com.sun.org.apache.xpath.internal.operations.*
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue


class DecimalValueTest {
    @Nested
    inner class InvokeTest {

        @Test
        fun `Byte input`() {
            val result = DecimalValue(28.toByte())
            assertThat(result, equalTo(expected))
        }

        @Test
        fun `Short input`() {
            val result = DecimalValue(28.toShort())
            assertThat(result, equalTo(expected))
        }

        @Test
        fun `Int input`() {
            val resul = DecimalValue(28)
            assertThat(resul, equalTo(expected))
        }

        @Test
        fun `Long input`() {
            val result = DecimalValue(28)
            assertThat(result, equalTo(expected))
        }

        @Test
        fun `Float input`() {
            val result = DecimalValue(28)
            assertThat(result, equalTo(expected))
        }

        @Test
        fun `Float nan input`() {
            val exception = assertThrows<IllegalArgumentException> {
                DecimalValue(Float.NaN)
            }
            assertThat(exception.message, Matchers.containsString("NaN"))
        }

        @Test
        fun `Float positive infinity input`() {
            val exception = assertThrows<IllegalArgumentException> {
                DecimalValue(Float.POSITIVE_INFINITY)
            }
            assertThat(exception.message, Matchers.containsString("Infinity"))
        }

        @Test
        fun `Float negative infinity input`() {
            val exception = assertThrows<IllegalArgumentException> {
                DecimalValue(Float.NEGATIVE_INFINITY)
            }
            assertThat(exception.message, Matchers.containsString("Infinity"))
        }

        @Test
        fun `Double input`() {
            val result = DecimalValue(28)
            assertThat(result, equalTo(expected))
        }

        @Test
        fun `Double nan input`() {
            val exception = assertThrows<IllegalArgumentException> {
                DecimalValue(Double.NaN)
            }
            assertThat(exception.message, Matchers.containsString("NaN"))
        }

        @Test
        fun `Double positive infinity input`() {
            val exception = assertThrows<IllegalArgumentException> {
                DecimalValue(Double.POSITIVE_INFINITY)
            }
            assertThat(exception.message, Matchers.containsString("Infinity"))
        }

        @Test
        fun `Double negative infinity input`() {
            val exception = assertThrows<IllegalArgumentException> {
                DecimalValue(Double.NEGATIVE_INFINITY)
            }
            assertThat(exception.message, Matchers.containsString("Infinity"))
        }

        @Test
        fun `String input`() {
            val result = DecimalValue("28")
            assertThat(result, equalTo(expected))
        }

        @Test
        fun `String nan input`() {
            val exception = assertThrows<IllegalArgumentException> {
                DecimalValue("NaN")
            }
            assertThat(exception.message, Matchers.containsString("NaN"))
        }

        @Test
        fun `String empty input`() {
            val exception = assertThrows<IllegalArgumentException> {
                DecimalValue("")
            }
            assertThat(exception.message, Matchers.containsString("NaN"))
        }

        @Test
        fun `String whitespaces input`() {
            val exception = assertThrows<IllegalArgumentException> {
                DecimalValue("    ")
            }
            assertThat(exception.message, Matchers.containsString("NaN"))
        }

        private val expected = DecimalValue(28)
    }

    @Nested
    inner class PlusTest {
        @Test
        fun ok() {
            val testInstance = DecimalValue(28)
            val otherInstance = DecimalValue(10)

            val result = testInstance.plus(otherInstance)
            assertThat(result, equalTo(DecimalValue(38)))

            assertThat(testInstance, equalTo(DecimalValue(28)))
            assertThat(otherInstance, equalTo(DecimalValue(10)))
        }
    }

    @Nested
    inner class MinusTest {
        @Test
        fun ok() {
            val testInstance = DecimalValue(28)
            val otherInstance = DecimalValue(10)

            val result = testInstance.minus(otherInstance)
            assertThat(result, equalTo(DecimalValue(18)))

            assertThat(testInstance, equalTo(DecimalValue(28)))
            assertThat(otherInstance, equalTo(DecimalValue(10)))
        }
    }

    @Nested
    inner class MultiplyTest {
        @Test
        fun ok() {
            val testInstance = DecimalValue(28)
            val otherInstance = DecimalValue(10)

            val result = testInstance.multiply(otherInstance)
            assertThat(result, equalTo(DecimalValue(280)))

            assertThat(testInstance, equalTo(DecimalValue(28)))
            assertThat(otherInstance, equalTo(DecimalValue(10)))
        }
    }

    @Nested
    inner class DivideTest {
        @Test
        fun ok() {
            val testInstance = DecimalValue(280)
            val otherInstance = DecimalValue(10)

            val result = testInstance.divide(otherInstance)
            assertThat(result, equalTo(DecimalValue(28)))

            assertThat(testInstance, equalTo(DecimalValue(280)))
            assertThat(otherInstance, equalTo(DecimalValue(10)))
        }
    }

    @Nested
    inner class RemainderTest {
        @Test
        fun ok() {
            val testInstance = DecimalValue("42.0021224")
            val divisor = DecimalValue("3.14152")
            val result = testInstance.remainder(divisor)

            assertThat(result, equalTo(DecimalValue("1.1623624")))

            assertThat(testInstance, equalTo(DecimalValue("42.0021224")))
            assertThat(divisor, equalTo(DecimalValue("3.14152")))
        }
    }

    @Nested
    inner class PowTest {
        @Test
        fun ok() {
            val testInstance = DecimalValue("10")
            val exponent = DecimalValue("2")
            val result = testInstance.pow(exponent)

            assertThat(result, equalTo(DecimalValue("100")))

            assertThat(testInstance, equalTo(DecimalValue("10")))
            assertThat(exponent, equalTo(DecimalValue("2")))
        }
    }

    @Nested
    inner class NegateTest {
        @Test
        fun `Negative value`() {
            val testInstance = DecimalValue(-28)

            val result = testInstance.negate()
            assertThat(result, equalTo(DecimalValue(28)))

            assertThat(testInstance, equalTo(DecimalValue(-28)))
        }

        @Test
        fun `Positive value`() {
            val testInstance = DecimalValue(28)

            val result = testInstance.negate()
            assertThat(result, equalTo(DecimalValue(-28)))

            assertThat(testInstance, equalTo(DecimalValue(28)))
        }
    }

    @Nested
    inner class AbsTest {
        @Test
        fun `Negative value`() {
            val testInstance = DecimalValue(-28)

            val result = testInstance.abs()
            assertThat(result, equalTo(DecimalValue(28)))

            assertThat(testInstance, equalTo(DecimalValue(-28)))
        }

        @Test
        fun `Positive value`() {
            val testInstance = DecimalValue(28)

            val result = testInstance.abs()
            assertThat(result, equalTo(DecimalValue(28)))

            assertThat(testInstance, equalTo(DecimalValue(28)))
        }
    }

    @TestFactory
    fun floor() = listOf(
        DecimalValue(0) to DecimalValue("0"),
        DecimalValue("3.14152") to DecimalValue("3"),
        DecimalValue("42.999999999") to DecimalValue("42")
    ).map { (testInstance, expected) ->
        DynamicTest.dynamicTest("floor of $testInstance") {
            val result = testInstance.floor()
            assertThat(result, equalTo(expected))
        }
    }

    @TestFactory
    fun ceil() = listOf(
        DecimalValue(0) to DecimalValue("0"),
        DecimalValue("3.14152") to DecimalValue("4"),
        DecimalValue("42.999999999") to DecimalValue("43")
    ).map { (testInstance, expected) ->
        DynamicTest.dynamicTest("ceil of $testInstance") {
            val result = testInstance.ceil()
            assertThat(result, equalTo(expected))
        }
    }

    @Nested
    inner class LnTest {
        @Test
        fun `Value is negative`() {
            val testInstance = DecimalValue(-42)
            val exception = assertThrows<IllegalArgumentException> {
                testInstance.ln()
            }
            assertThat(exception.message, Matchers.containsString("Value must >= 1"))
        }

        @Test
        fun `Value is 0`() {
            val testInstance = DecimalValue(0)
            val exception = assertThrows<IllegalArgumentException> {
                testInstance.ln()
            }
            assertThat(exception.message, Matchers.containsString("Value must >= 1"))
        }

        @Test

        fun `Value is 1`() {
            val testInstance = DecimalValue(1)

            val result = testInstance.ln()
            assertThat(result, equalTo(DecimalValue(0)))

            assertThat(testInstance, equalTo(DecimalValue(1)))
        }

        @TestFactory
        fun test() = listOf(
            DecimalValue(2) to DecimalValue("0.6931471805599453094172321214581766"),
            DecimalValue("1234") to DecimalValue("7.118016204465333123414803800068370"),
            DecimalValue("1234.09901234123") to DecimalValue("7.118096438151897579984386821678212"),
            DecimalValue("987654") to DecimalValue("13.80308771296566413828433949825021"),
            DecimalValue("9876543") to DecimalValue("16.10567236153744431962426738978479"),
            DecimalValue("100000") to DecimalValue("11.51292546497022842008995727342182"),
            DecimalValue("1000000") to DecimalValue("13.81551055796427410410675612270492")
        ).map { (testInstance, expected) ->
            DynamicTest.dynamicTest("ln of $testInstance") {
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
            val testInstance = DecimalValue(-42)
            val exception = assertThrows<IllegalStateException> {
                testInstance.sqrt()
            }
            assertThat(exception.message, Matchers.containsString("Value must >= 0"))
        }

        @TestFactory
        fun sqrt() = listOf(
            DecimalValue(0) to DecimalValue("0"),
            DecimalValue(1) to DecimalValue("1"),
            DecimalValue(2) to DecimalValue("1.414213562373095048801688724209698"),
            DecimalValue("1234.09901234123") to DecimalValue("35.12974540672377261172508927243486"),
            DecimalValue("987654") to DecimalValue("993.8078285060950353793489213512089"),
            DecimalValue("9876543") to DecimalValue("3142.696771882390819470725583679575"),
            DecimalValue("100000") to DecimalValue("316.2277660168379331998893544432719"),
            DecimalValue("1000000") to DecimalValue("1000")
        ).map { (testInstance, expected) ->
            DynamicTest.dynamicTest("sqrt of $testInstance") {
                val result = testInstance.sqrt()
                assertThat(result, equalTo(expected))
            }
        }
    }

    @Nested
    inner class IsLessThanTest {
        @Test
        fun `Less Than`() {
            val testInstance = DecimalValue(5)
            val otherInstance = DecimalValue(10)
            assertTrue(testInstance.isLessThan(otherInstance))
        }

        @Test
        fun `Equal to`() {
            val testInstance = DecimalValue(5)
            val otherInstance = DecimalValue(5)
            assertFalse(testInstance.isLessThan(otherInstance))
        }

        @Test
        fun `Greater Than`() {
            val testInstance = DecimalValue(10)
            val otherInstance = DecimalValue(5)
            assertFalse(testInstance.isLessThan(otherInstance))
        }
    }

    @Nested
    inner class IsLessThanEqualTest {
        @Test
        fun `Less Than`() {
            val testInstance = DecimalValue(5)
            val otherInstance = DecimalValue(10)
            assertTrue(testInstance.isLessThanEqual(otherInstance))
        }

        @Test
        fun `Equal to`() {
            val testInstance = DecimalValue(5)
            val otherInstance = DecimalValue(5)
            assertTrue(testInstance.isLessThanEqual(otherInstance))
        }

        @Test
        fun `Greater Than`() {
            val testInstance = DecimalValue(10)
            val otherInstance = DecimalValue(5)
            assertFalse(testInstance.isLessThanEqual(otherInstance))
        }
    }

    @Nested
    inner class IsGreaterThanTest {
        @Test
        fun `Less Than`() {
            val testInstance = DecimalValue(5)
            val otherInstance = DecimalValue(10)
            assertFalse(testInstance.isGreaterThan(otherInstance))
        }

        @Test
        fun `Equal to`() {
            val testInstance = DecimalValue(5)
            val otherInstance = DecimalValue(5)
            assertFalse(testInstance.isGreaterThan(otherInstance))
        }

        @Test
        fun `Greater Than`() {
            val testInstance = DecimalValue(10)
            val otherInstance = DecimalValue(5)
            assertTrue(testInstance.isGreaterThan(otherInstance))
        }
    }

    @Nested
    inner class IsGreaterThanEqualTest {
        @Test
        fun `Less Than`() {
            val testInstance = DecimalValue(5)
            val otherInstance = DecimalValue(10)
            assertFalse(testInstance.isGreaterThanEqual(otherInstance))
        }

        @Test
        fun `Equal to`() {
            val testInstance = DecimalValue(5)
            val otherInstance = DecimalValue(5)
            assertTrue(testInstance.isGreaterThanEqual(otherInstance))
        }

        @Test
        fun `Greater Than`() {
            val testInstance = DecimalValue(10)
            val otherInstance = DecimalValue(5)
            assertTrue(testInstance.isGreaterThanEqual(otherInstance))
        }
    }

    @Nested
    inner class IsNegativeTest {
        @Test
        fun `Negative number`() {
            val testInstance = DecimalValue(-10)
            assertTrue(testInstance.isNegative())
        }

        @Test
        fun `Zero`() {
            val testInstance = DecimalValue.Zero
            assertFalse(testInstance.isNegative())
        }

        @Test
        fun `Positive number`() {
            val testInstance = DecimalValue(10)
            assertFalse(testInstance.isNegative())
        }
    }

    @Nested
    inner class IsPositiveTest {
        @Test
        fun `Negative number`() {
            val testInstance = DecimalValue(-10)
            assertFalse(testInstance.isPositive())
        }

        @Test
        fun `Zero`() {
            val testInstance = DecimalValue.Zero
            assertFalse(testInstance.isPositive())
        }

        @Test
        fun `Positive number`() {
            val testInstance = DecimalValue(10)
            assertTrue(testInstance.isPositive())
        }
    }

    @Nested
    inner class IsZeroTest {
        @Test
        fun `Negative number`() {
            val testInstance = DecimalValue(-10)
            assertFalse(testInstance.isZero())
        }

        @Test
        fun `Zero`() {
            val testInstance = DecimalValue.Zero
            assertTrue(testInstance.isZero())
        }

        @Test
        fun `Positive number`() {
            val testInstance = DecimalValue(10)
            assertFalse(testInstance.isZero())
        }
    }

    @Nested
    inner class ToByteTest {
        @Test
        fun ok() {
            val testInstance = DecimalValue(28)
            assertThat(testInstance.toByte(), equalTo(28))
        }
    }

    @Nested
    inner class ToShortTest {
        @Test
        fun ok() {
            val testInstance = DecimalValue(28)
            assertThat(testInstance.toShort(), equalTo(28))
        }
    }

    @Nested
    inner class ToIntTest {
        @Test
        fun ok() {
            val testInstance = DecimalValue(28)
            assertThat(testInstance.toInt(), equalTo(28))
        }
    }

    @Nested
    inner class ToLongTest {
        @Test
        fun ok() {
            val testInstance = DecimalValue(28)
            assertThat(testInstance.toLong(), equalTo(28))
        }
    }

    @Nested
    inner class ToFloatTest {
        @Test
        fun ok() {
            val testInstance = DecimalValue(28.10)
            assertThat(testInstance.toFloat(), equalTo(28.10f))
        }
    }

    @Nested
    inner class ToDoubleTest {
        @Test
        fun ok() {
            val testInstance = DecimalValue(28.10)
            assertThat(testInstance.toDouble(), equalTo(28.10))
        }
    }

    @TestFactory
    fun serialization() = listOf(
        ValueSerializationFixture.generateTestCases(DecimalValue.Zero, """{"type":"DecimalValue","value":"0"}"""),
        ValueSerializationFixture.generateTestCases(
            DecimalValue(-12.324),
            """{"type":"DecimalValue","value":"-12.324"}"""
        ),
        ValueSerializationFixture.generateTestCases(
            DecimalValue("123456789.987654321"),
            """{"type":"DecimalValue","value":"123456789.987654321"}"""
        )
    ).flatten()
}
