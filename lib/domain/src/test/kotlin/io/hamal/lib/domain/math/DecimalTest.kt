package io.hamal.lib.domain.math

import io.hamal.lib.domain.Tuple2
import io.hamal.lib.domain.math.Decimal
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class DecimalTest {

    @Nested
    @DisplayName("invoke()")
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
    @DisplayName("plus()")
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
    @DisplayName("minus()")
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
    @DisplayName("multiply()")
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
    @DisplayName("divide()")
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
    @DisplayName("remainder()")
    inner class RemainderTest {
        @Test
        fun ok() {
            val testInstance = Decimal("42.0021224");
            val divisor = Decimal("3.14152");
            val result = testInstance.remainder(divisor);

            assertThat(result, equalTo(Decimal("1.1623624")));

            assertThat(testInstance, equalTo(Decimal("42.0021224")));
            assertThat(divisor, equalTo(Decimal("3.14152")));
        }
    }

    @Nested
    @DisplayName("negate()")
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
    @DisplayName("abs()")
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

    @Nested
    @DisplayName("floor()")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class FloorTest {
        private fun provider(): List<Tuple2<Decimal, Decimal>> {
            return listOf(
                Tuple2(Decimal(0), Decimal("0")),
                Tuple2(Decimal("3.14152"), Decimal("3")),
                Tuple2(Decimal("42.999999999"), Decimal("42"))
            );
        }

        @ParameterizedTest(name = "#{index} - Test {0}")
        @MethodSource("provider")
        fun test(arg: Tuple2<Decimal, Decimal>) {
            val testInstance = arg._1;
            val expected = arg._2;
            val result = testInstance.floor();

            assertThat(result, equalTo(expected));
        }
    }

    @Nested
    @DisplayName("ceil()")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class CeilTest {

        private fun provider(): List<Tuple2<Decimal, Decimal>> {
            return listOf(
                Tuple2(Decimal(0), Decimal("0")),
                Tuple2(Decimal("3.14152"), Decimal("4")),
                Tuple2(Decimal("42.999999999"), Decimal("43"))
            );
        }

        @ParameterizedTest(name = "#{index} - Test {0}")
        @MethodSource("provider")
        fun test(arg: Tuple2<Decimal, Decimal>) {
            val testInstance = arg._1;
            val expected = arg._2;
            val result = testInstance.ceil();

            assertThat(result, equalTo(expected));
        }
    }

    @Nested
    @DisplayName("ln()")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class LnTest {

        @Test
        fun `Value is negative`() {
            val testInstance = Decimal(-42);
            val exception = assertThrows<IllegalArgumentException> {
                testInstance.ln()
            };
            assertThat(exception.message, containsString("Value must >= 1"));
        }

        @Test
        fun `Value is 0`() {
            val testInstance = Decimal(0);
            val exception = assertThrows<IllegalArgumentException> {
                testInstance.ln()
            };
            assertThat(exception.message, containsString("Value must >= 1"));
        }

        @Test
        @DisplayName("value is one")
        fun `Value is 1`() {
            val testInstance = Decimal(1);

            val result = testInstance.ln();
            assertThat(result, equalTo(Decimal(0)));

            assertThat(testInstance, equalTo(Decimal(1)));
        }

        private fun provider(): List<Tuple2<Decimal, Decimal>> {
            return listOf(
                Tuple2(Decimal(2), Decimal("0.6931471805599450")),
                Tuple2(Decimal("1234"), Decimal("7.118016204465331")),
                Tuple2(Decimal("1234.09901234123"), Decimal("7.118096438151894")),
                Tuple2(Decimal("987654"), Decimal("13.80308771296568")),
                Tuple2(Decimal("9876543"), Decimal("16.10567236153747")),
                Tuple2(Decimal("100000"), Decimal("11.51292546497022")),
                Tuple2(Decimal("1000000"), Decimal("13.8155105579642741"))
            );
        }

        @ParameterizedTest(name = "#{index} - Test {0}")
        @MethodSource("provider")
        fun test(arg: Tuple2<Decimal, Decimal>) {
            val testInstance = arg._1;
            val expected = arg._2
            val result = testInstance.ln();

            assertThat(result, equalTo(expected));
        }
    }

    @Nested
    @DisplayName("sqrt()")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class SqrtTest {

        @Test
        fun `Value is negative`() {
            val testInstance = Decimal(-42);
            val exception = assertThrows<IllegalStateException> {
                testInstance.sqrt()
            }
            assertThat(exception.message, containsString("Value must >= 0"));
        }

        private fun provider(): List<Tuple2<Decimal, Decimal>> {
            return listOf(
                Tuple2(Decimal(0), Decimal("0")),
                Tuple2(Decimal(1), Decimal("1")),
                Tuple2(Decimal(2), Decimal("1.414213562373095")),
                Tuple2(Decimal("1234.09901234123"), Decimal("35.1297454067237726")),
                Tuple2(Decimal("987654"), Decimal("993.807828506095")),
                Tuple2(Decimal("9876543"), Decimal("3142.69677188239082")),
                Tuple2(Decimal("100000"), Decimal("316.227766016837933")),
                Tuple2(Decimal("1000000"), Decimal("1000"))
            );
        }

        @ParameterizedTest(name = "#{index} - Test {0}")
        @MethodSource("provider")
        fun test(arg: Tuple2<Decimal, Decimal>) {
            val testInstance = arg._1;
            val expected = arg._2;
            val result = testInstance.sqrt();

            assertThat(result, equalTo(expected));
        }

    }

    @Nested
    @DisplayName("isLessThan()")
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
    @DisplayName("isLessThanEqual()")
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
    @DisplayName("isGreaterThan()")
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
    @DisplayName("isGreaterThanEqual()")
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
    @DisplayName("isNegative()")
    inner class IsNegativeTest {
        @Test
        fun `Negative number`() {
            val testInstance = Decimal(-10)
            assertTrue(testInstance.isNegative())
        }

        @Test
        fun `Zero`() {
            val testInstance = Decimal.ZERO
            assertFalse(testInstance.isNegative())
        }

        @Test
        fun `Positive number`() {
            val testInstance = Decimal(10)
            assertFalse(testInstance.isNegative())
        }
    }

    @Nested
    @DisplayName("isPositive()")
    inner class IsPositiveTest {
        @Test
        fun `Negative number`() {
            val testInstance = Decimal(-10)
            assertFalse(testInstance.isPositive())
        }

        @Test
        fun `Zero`() {
            val testInstance = Decimal.ZERO
            assertFalse(testInstance.isPositive())
        }

        @Test
        fun `Positive number`() {
            val testInstance = Decimal(10)
            assertTrue(testInstance.isPositive())
        }
    }

    @Nested
    @DisplayName("isZero()")
    inner class IsZeroTest {
        @Test
        fun `Negative number`() {
            val testInstance = Decimal(-10)
            assertFalse(testInstance.isZero())
        }

        @Test
        fun `Zero`() {
            val testInstance = Decimal.ZERO
            assertTrue(testInstance.isZero())
        }

        @Test
        fun `Positive number`() {
            val testInstance = Decimal(10)
            assertFalse(testInstance.isZero())
        }
    }

    @Nested
    @DisplayName("toByte()")
    inner class ToByteTest {
        @Test
        fun ok() {
            val testInstance = Decimal(28)
            assertThat(testInstance.toByte(), equalTo(28))
        }
    }

    @Nested
    @DisplayName("toShort()")
    inner class ToShortTest {
        @Test
        fun ok() {
            val testInstance = Decimal(28)
            assertThat(testInstance.toShort(), equalTo(28))
        }
    }

    @Nested
    @DisplayName("toInt()")
    inner class ToIntTest {
        @Test
        fun ok() {
            val testInstance = Decimal(28)
            assertThat(testInstance.toInt(), equalTo(28))
        }
    }

    @Nested
    @DisplayName("toLong()")
    inner class ToLongTest {
        @Test
        fun ok() {
            val testInstance = Decimal(28)
            assertThat(testInstance.toLong(), equalTo(28))
        }
    }

    @Nested
    @DisplayName("toFloat()")
    inner class ToFloatTest {
        @Test
        fun ok() {
            val testInstance = Decimal(28.10)
            assertThat(testInstance.toFloat(), equalTo(28.10f))
        }
    }

    @Nested
    @DisplayName("toDouble()")
    inner class ToDoubleTest {
        @Test
        fun ok() {
            val testInstance = Decimal(28.10)
            assertThat(testInstance.toDouble(), equalTo(28.10.toDouble()))
        }
    }
}