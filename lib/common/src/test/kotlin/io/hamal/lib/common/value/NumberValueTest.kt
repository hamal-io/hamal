package io.hamal.lib.common.value

import io.hamal.lib.common.value.ValueOperation.Type.*
import io.hamal.lib.common.value.ValueSerializationFixture.generateTestCases
import io.hamal.lib.domain.Tuple3
import io.hamal.lib.domain.Tuple4
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DynamicTest.dynamicTest

class NumberValueTest {
    @Nested
    inner class InvokeTest {

        @Test
        fun `Byte input`() {
            val result = NumberValue(28.toByte())
            assertThat(result, equalTo(expected))
        }

        @Test
        fun `Short input`() {
            val result = NumberValue(28.toShort())
            assertThat(result, equalTo(expected))
        }

        @Test
        fun `Int input`() {
            val resul = NumberValue(28)
            assertThat(resul, equalTo(expected))
        }

        @Test
        fun `Long input`() {
            val result = NumberValue(28)
            assertThat(result, equalTo(expected))
        }

        @Test
        fun `Float input`() {
            val result = NumberValue(28)
            assertThat(result, equalTo(expected))
        }

        @Test
        fun `Float nan input`() {
            val exception = assertThrows<IllegalArgumentException> {
                NumberValue(Float.NaN)
            }
            assertThat(exception.message, containsString("NaN"))
        }

        @Test
        fun `Float positive infinity input`() {
            val exception = assertThrows<IllegalArgumentException> {
                NumberValue(Float.POSITIVE_INFINITY)
            }
            assertThat(exception.message, containsString("Infinity"))
        }

        @Test
        fun `Float negative infinity input`() {
            val exception = assertThrows<IllegalArgumentException> {
                NumberValue(Float.NEGATIVE_INFINITY)
            }
            assertThat(exception.message, containsString("Infinity"))
        }

        @Test
        fun `Double input`() {
            val result = NumberValue(28)
            assertThat(result, equalTo(expected))
        }

        @Test
        fun `Double nan input`() {
            val exception = assertThrows<IllegalArgumentException> {
                NumberValue(Double.NaN)
            }
            assertThat(exception.message, containsString("NaN"))
        }

        @Test
        fun `Double positive infinity input`() {
            val exception = assertThrows<IllegalArgumentException> {
                NumberValue(Double.POSITIVE_INFINITY)
            }
            assertThat(exception.message, containsString("Infinity"))
        }

        @Test
        fun `Double negative infinity input`() {
            val exception = assertThrows<IllegalArgumentException> {
                NumberValue(Double.NEGATIVE_INFINITY)
            }
            assertThat(exception.message, containsString("Infinity"))
        }

        @Test
        fun `String input`() {
            val result = NumberValue("28")
            assertThat(result, equalTo(expected))
        }

        @Test
        fun `String nan input`() {
            val exception = assertThrows<IllegalArgumentException> {
                NumberValue("NaN")
            }
            assertThat(exception.message, containsString("NaN"))
        }

        @Test
        fun `String empty input`() {
            val exception = assertThrows<IllegalArgumentException> {
                NumberValue("")
            }
            assertThat(exception.message, containsString("NaN"))
        }

        @Test
        fun `String whitespaces input`() {
            val exception = assertThrows<IllegalArgumentException> {
                NumberValue("    ")
            }
            assertThat(exception.message, containsString("NaN"))
        }

        private val expected = NumberValue(28)
    }

    @Nested
    inner class PlusTest {
        @Test
        fun ok() {
            val testInstance = NumberValue(28)
            val otherInstance = NumberValue(10)

            val result = testInstance.plus(otherInstance)
            assertThat(result, equalTo(NumberValue(38)))

            assertThat(testInstance, equalTo(NumberValue(28)))
            assertThat(otherInstance, equalTo(NumberValue(10)))
        }
    }

    @Nested
    inner class MinusTest {
        @Test
        fun ok() {
            val testInstance = NumberValue(28)
            val otherInstance = NumberValue(10)

            val result = testInstance.minus(otherInstance)
            assertThat(result, equalTo(NumberValue(18)))

            assertThat(testInstance, equalTo(NumberValue(28)))
            assertThat(otherInstance, equalTo(NumberValue(10)))
        }
    }

    @Nested
    inner class MultiplyTest {
        @Test
        fun ok() {
            val testInstance = NumberValue(28)
            val otherInstance = NumberValue(10)

            val result = testInstance.multiply(otherInstance)
            assertThat(result, equalTo(NumberValue(280)))

            assertThat(testInstance, equalTo(NumberValue(28)))
            assertThat(otherInstance, equalTo(NumberValue(10)))
        }
    }

    @Nested
    inner class DivideTest {
        @Test
        fun ok() {
            val testInstance = NumberValue(280)
            val otherInstance = NumberValue(10)

            val result = testInstance.divide(otherInstance)
            assertThat(result, equalTo(NumberValue(28)))

            assertThat(testInstance, equalTo(NumberValue(280)))
            assertThat(otherInstance, equalTo(NumberValue(10)))
        }
    }

    @Nested
    inner class RemainderTest {
        @Test
        fun ok() {
            val testInstance = NumberValue("42.0021224")
            val divisor = NumberValue("3.14152")
            val result = testInstance.remainder(divisor)

            assertThat(result, equalTo(NumberValue("1.1623624")))

            assertThat(testInstance, equalTo(NumberValue("42.0021224")))
            assertThat(divisor, equalTo(NumberValue("3.14152")))
        }
    }

    @Nested
    inner class PowTest {
        @Test
        fun ok() {
            val testInstance = NumberValue("10")
            val exponent = NumberValue("2")
            val result = testInstance.pow(exponent)

            assertThat(result, equalTo(NumberValue("100")))

            assertThat(testInstance, equalTo(NumberValue("10")))
            assertThat(exponent, equalTo(NumberValue("2")))
        }
    }

    @Nested
    inner class NegateTest {
        @Test
        fun `Negative value`() {
            val testInstance = NumberValue(-28)

            val result = testInstance.negate()
            assertThat(result, equalTo(NumberValue(28)))

            assertThat(testInstance, equalTo(NumberValue(-28)))
        }

        @Test
        fun `Positive value`() {
            val testInstance = NumberValue(28)

            val result = testInstance.negate()
            assertThat(result, equalTo(NumberValue(-28)))

            assertThat(testInstance, equalTo(NumberValue(28)))
        }
    }

    @Nested
    inner class AbsTest {
        @Test
        fun `Negative value`() {
            val testInstance = NumberValue(-28)

            val result = testInstance.abs()
            assertThat(result, equalTo(NumberValue(28)))

            assertThat(testInstance, equalTo(NumberValue(-28)))
        }

        @Test
        fun `Positive value`() {
            val testInstance = NumberValue(28)

            val result = testInstance.abs()
            assertThat(result, equalTo(NumberValue(28)))

            assertThat(testInstance, equalTo(NumberValue(28)))
        }
    }

    @TestFactory
    fun floor() = listOf(
        NumberValue(0) to NumberValue("0"),
        NumberValue("3.14152") to NumberValue("3"),
        NumberValue("42.999999999") to NumberValue("42")
    ).map { (testInstance, expected) ->
        dynamicTest("floor of $testInstance") {
            val result = testInstance.floor()
            assertThat(result, equalTo(expected))
        }
    }

    @TestFactory
    fun ceil() = listOf(
        NumberValue(0) to NumberValue("0"),
        NumberValue("3.14152") to NumberValue("4"),
        NumberValue("42.999999999") to NumberValue("43")
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
            val testInstance = NumberValue(-42)
            val exception = assertThrows<IllegalArgumentException> {
                testInstance.ln()
            }
            assertThat(exception.message, containsString("Value must >= 1"))
        }

        @Test
        fun `Value is 0`() {
            val testInstance = NumberValue(0)
            val exception = assertThrows<IllegalArgumentException> {
                testInstance.ln()
            }
            assertThat(exception.message, containsString("Value must >= 1"))
        }

        @Test

        fun `Value is 1`() {
            val testInstance = NumberValue(1)

            val result = testInstance.ln()
            assertThat(result, equalTo(NumberValue(0)))

            assertThat(testInstance, equalTo(NumberValue(1)))
        }

        @TestFactory
        fun test() = listOf(
            NumberValue(2) to NumberValue("0.6931471805599453094172321214581766"),
            NumberValue("1234") to NumberValue("7.118016204465333123414803800068370"),
            NumberValue("1234.09901234123") to NumberValue("7.118096438151897579984386821678212"),
            NumberValue("987654") to NumberValue("13.80308771296566413828433949825021"),
            NumberValue("9876543") to NumberValue("16.10567236153744431962426738978479"),
            NumberValue("100000") to NumberValue("11.51292546497022842008995727342182"),
            NumberValue("1000000") to NumberValue("13.81551055796427410410675612270492")
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
            val testInstance = NumberValue(-42)
            val exception = assertThrows<IllegalStateException> {
                testInstance.sqrt()
            }
            assertThat(exception.message, containsString("Value must >= 0"))
        }

        @TestFactory
        fun sqrt() = listOf(
            NumberValue(0) to NumberValue("0"),
            NumberValue(1) to NumberValue("1"),
            NumberValue(2) to NumberValue("1.414213562373095048801688724209698"),
            NumberValue("1234.09901234123") to NumberValue("35.12974540672377261172508927243486"),
            NumberValue("987654") to NumberValue("993.8078285060950353793489213512089"),
            NumberValue("9876543") to NumberValue("3142.696771882390819470725583679575"),
            NumberValue("100000") to NumberValue("316.2277660168379331998893544432719"),
            NumberValue("1000000") to NumberValue("1000")
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
            val testInstance = NumberValue(5)
            val otherInstance = NumberValue(10)
            assertTrue(testInstance.isLessThan(otherInstance))
        }

        @Test
        fun `Equal to`() {
            val testInstance = NumberValue(5)
            val otherInstance = NumberValue(5)
            assertFalse(testInstance.isLessThan(otherInstance))
        }

        @Test
        fun `Greater Than`() {
            val testInstance = NumberValue(10)
            val otherInstance = NumberValue(5)
            assertFalse(testInstance.isLessThan(otherInstance))
        }
    }

    @Nested
    inner class IsLessThanEqualTest {
        @Test
        fun `Less Than`() {
            val testInstance = NumberValue(5)
            val otherInstance = NumberValue(10)
            assertTrue(testInstance.isLessThanEqual(otherInstance))
        }

        @Test
        fun `Equal to`() {
            val testInstance = NumberValue(5)
            val otherInstance = NumberValue(5)
            assertTrue(testInstance.isLessThanEqual(otherInstance))
        }

        @Test
        fun `Greater Than`() {
            val testInstance = NumberValue(10)
            val otherInstance = NumberValue(5)
            assertFalse(testInstance.isLessThanEqual(otherInstance))
        }
    }

    @Nested
    inner class IsGreaterThanTest {
        @Test
        fun `Less Than`() {
            val testInstance = NumberValue(5)
            val otherInstance = NumberValue(10)
            assertFalse(testInstance.isGreaterThan(otherInstance))
        }

        @Test
        fun `Equal to`() {
            val testInstance = NumberValue(5)
            val otherInstance = NumberValue(5)
            assertFalse(testInstance.isGreaterThan(otherInstance))
        }

        @Test
        fun `Greater Than`() {
            val testInstance = NumberValue(10)
            val otherInstance = NumberValue(5)
            assertTrue(testInstance.isGreaterThan(otherInstance))
        }
    }

    @Nested
    inner class IsGreaterThanEqualTest {
        @Test
        fun `Less Than`() {
            val testInstance = NumberValue(5)
            val otherInstance = NumberValue(10)
            assertFalse(testInstance.isGreaterThanEqual(otherInstance))
        }

        @Test
        fun `Equal to`() {
            val testInstance = NumberValue(5)
            val otherInstance = NumberValue(5)
            assertTrue(testInstance.isGreaterThanEqual(otherInstance))
        }

        @Test
        fun `Greater Than`() {
            val testInstance = NumberValue(10)
            val otherInstance = NumberValue(5)
            assertTrue(testInstance.isGreaterThanEqual(otherInstance))
        }
    }

    @Nested
    inner class IsNegativeTest {
        @Test
        fun `Negative number`() {
            val testInstance = NumberValue(-10)
            assertTrue(testInstance.isNegative())
        }

        @Test
        fun `Zero`() {
            val testInstance = NumberValue.Zero
            assertFalse(testInstance.isNegative())
        }

        @Test
        fun `Positive number`() {
            val testInstance = NumberValue(10)
            assertFalse(testInstance.isNegative())
        }
    }

    @Nested
    inner class IsPositiveTest {
        @Test
        fun `Negative number`() {
            val testInstance = NumberValue(-10)
            assertFalse(testInstance.isPositive())
        }

        @Test
        fun `Zero`() {
            val testInstance = NumberValue.Zero
            assertFalse(testInstance.isPositive())
        }

        @Test
        fun `Positive number`() {
            val testInstance = NumberValue(10)
            assertTrue(testInstance.isPositive())
        }
    }

    @Nested
    inner class IsZeroTest {
        @Test
        fun `Negative number`() {
            val testInstance = NumberValue(-10)
            assertFalse(testInstance.isZero())
        }

        @Test
        fun `Zero`() {
            val testInstance = NumberValue.Zero
            assertTrue(testInstance.isZero())
        }

        @Test
        fun `Positive number`() {
            val testInstance = NumberValue(10)
            assertFalse(testInstance.isZero())
        }
    }

    @Nested
    inner class ToByteTest {
        @Test
        fun ok() {
            val testInstance = NumberValue(28)
            assertThat(testInstance.toByte(), equalTo(28))
        }
    }

    @Nested
    inner class ToShortTest {
        @Test
        fun ok() {
            val testInstance = NumberValue(28)
            assertThat(testInstance.toShort(), equalTo(28))
        }
    }

    @Nested
    inner class ToIntTest {
        @Test
        fun ok() {
            val testInstance = NumberValue(28)
            assertThat(testInstance.toInt(), equalTo(28))
        }
    }

    @Nested
    inner class ToLongTest {
        @Test
        fun ok() {
            val testInstance = NumberValue(28)
            assertThat(testInstance.toLong(), equalTo(28))
        }
    }

    @Nested
    inner class ToFloatTest {
        @Test
        fun ok() {
            val testInstance = NumberValue(28.10)
            assertThat(testInstance.toFloat(), equalTo(28.10f))
        }
    }

    @Nested
    inner class ToDoubleTest {
        @Test
        fun ok() {
            val testInstance = NumberValue(28.10)
            assertThat(testInstance.toDouble(), equalTo(28.10))
        }
    }

    @TestFactory
    fun Serialization() = listOf(
        generateTestCases(NumberValue.Zero, """{"type":"NumberValue","value":"0"}"""),
        generateTestCases(NumberValue(-12.324), """{"type":"NumberValue","value":"-12.324"}"""),
        generateTestCases(
            NumberValue("123456789.987654321"),
            """{"type":"NumberValue","value":"123456789.987654321"}"""
        )
    ).flatten()
}


class DefaultNumberMetaTableTest {
    @Test
    fun `Every operation is covered`() {
        assertThat(DefaultNumberMetaTable.operations, hasSize(14))
    }

    @TestFactory
    fun infix() = listOf<Tuple4<NumberValue, ValueOperation.Type, Value, Value>>(

        Tuple4(NumberValue(1), Add, NumberValue(3), NumberValue(4)),
        Tuple4(NumberValue(10), Div, NumberValue(5), NumberValue(2)),
        Tuple4(NumberValue(10), Eq, NumberValue(10), TrueValue),
        Tuple4(NumberValue(12), Eq, NumberValue(10), FalseValue),

        Tuple4(NumberValue(12), Gt, NumberValue(10), TrueValue),
        Tuple4(NumberValue(12), Gt, NumberValue(12), FalseValue),
        Tuple4(NumberValue(12), Gt, NumberValue(14), FalseValue),

        Tuple4(NumberValue(12), Gte, NumberValue(10), TrueValue),
        Tuple4(NumberValue(12), Gte, NumberValue(12), TrueValue),
        Tuple4(NumberValue(12), Gte, NumberValue(14), FalseValue),

        Tuple4(NumberValue(12), Lt, NumberValue(10), FalseValue),
        Tuple4(NumberValue(12), Lt, NumberValue(12), FalseValue),
        Tuple4(NumberValue(12), Lt, NumberValue(14), TrueValue),

        Tuple4(NumberValue(12), Lte, NumberValue(10), FalseValue),
        Tuple4(NumberValue(12), Lte, NumberValue(12), TrueValue),
        Tuple4(NumberValue(12), Lte, NumberValue(14), TrueValue),

        Tuple4(NumberValue(9), Mod, NumberValue(3), NumberValue(0)),
        Tuple4(NumberValue(9), Mul, NumberValue(3), NumberValue(27)),
        Tuple4(NumberValue(2), Pow, NumberValue(10), NumberValue(1024)),

        Tuple4(NumberValue(1), Sub, NumberValue(3), NumberValue(-2)),

        // Nil
        Tuple4(NumberValue(1), Eq, NilValue, FalseValue),
        Tuple4(NumberValue(1), Neq, NilValue, TrueValue),


        ).map { (self, operator, other, expected) ->
        dynamicTest("$self $operator $other = $expected") {
            val infixOp = self.findInfixOperation(operator, other.type())!!
            val result = infixOp(self, other)
            assertThat(result, equalTo(expected))
        }
    }

    @TestFactory
    fun prefix() = listOf<Tuple3<ValueOperation.Type, NumberValue, Value>>(

        Tuple3(Negate, NumberValue(1), NumberValue(-1)),
        Tuple3(Negate, NumberValue(-1), NumberValue(1)),

        ).map { (operator, self, expected) ->
        dynamicTest("$operator $self = $expected") {
            val prefixOp = self.findPrefixOperation(operator)!!
            val result = prefixOp(self)
            assertThat(result, equalTo(expected))
        }
    }

}