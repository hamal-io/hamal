package io.hamal.lib.script.api.value

import io.hamal.lib.domain.Tuple4
import io.hamal.lib.script.api.value.ValueOperator.Type.Eq
import io.hamal.lib.script.api.value.ValueSerializationFixture.generateTestCases
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory


class TableValueTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(TableValue(), """{"type":"TableValue"}"""),
        generateTestCases(
            TableValue(
                "key" to StringValue("value")
            ),
            """{"type":"TableValue","entries":{"key":{"type":"StringValue","value":"value"}}}""".trimIndent()
        ),
        generateTestCases(
            TableValue(
                23 to NumberValue(34)
            ),
            """{"type":"TableValue","entries":{"23":{"type":"NumberValue","value":"34"}}}"""
        ),
    ).flatten()
}

class DefaultTableValueMetaTableTest {
    @Test
    fun `Every operation is covered`() {
        assertThat(DefaultTableValueMetaTable.operators, hasSize(1))
    }

    @Test
    fun `Test type`() {
        assertThat(DefaultTableValueMetaTable.type, equalTo("table"))
    }

    @TestFactory
    fun infix() = listOf<Tuple4<TableValue, ValueOperator.Type, Value, Value>>(
        Tuple4(TableValue(), Eq, TableValue(), TrueValue),
        Tuple4(TableValue("ham" to "al"), Eq, TableValue(), FalseValue),
        Tuple4(TableValue(), Eq, TableValue("ham" to "al"), FalseValue),
        Tuple4(TableValue("ham" to "al"), Eq, TableValue("ham" to "al"), TrueValue),
        Tuple4(TableValue("ham" to "1337"), Eq, TableValue("ham" to "al"), FalseValue),
        Tuple4(TableValue("ham" to "al"), Eq, TableValue("ham" to "1337"), FalseValue),

        ).map { (self, operator, other, expected) ->
        DynamicTest.dynamicTest("$self $operator $other = $expected") {
            val infixOp = self.findInfixOperation(operator, other.type())!!
            val result = infixOp(self, other)
            assertThat(result, equalTo(expected))
        }
    }
}