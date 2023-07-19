package io.hamal.lib.script.api.value

import io.hamal.lib.domain.Tuple4
import io.hamal.lib.kua.value.ValueOperator.Type.Eq
import io.hamal.lib.kua.value.ValueOperator.Type.Neq
import io.hamal.lib.kua.value.ValueSerializationFixture.generateTestCases
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class StringValueTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(StringValue("hamal"), """{"type":"StringValue","value":"hamal"}"""),
    ).flatten()
}

class DefaultStringValueMetaTableTest {
    @Test
    fun `Every operation is covered`() {
        assertThat(DefaultStringValueMetaTable.operators, hasSize(2))
    }

    @Test
    fun `Test type`() {
        assertThat(DefaultStringValueMetaTable.type, equalTo("string"))
    }

    @TestFactory
    fun infix() = listOf<Tuple4<StringValue, ValueOperator.Type, Value, Value>>(
        Tuple4(StringValue("h4m41"), Eq, StringValue("h4m41"), TrueValue),
        Tuple4(StringValue("h4m41"), Eq, StringValue("HAMAL"), FalseValue),
        Tuple4(StringValue("h4m41"), Neq, StringValue("h4m41"), FalseValue),
        Tuple4(StringValue("h4m41"), Neq, StringValue("HAMAL"), TrueValue),
    ).map { (self, operator, other, expected) ->
        DynamicTest.dynamicTest("$self $operator $other = $expected") {
            val infixOp = self.findInfixOperation(operator, other.type())!!
            val result = infixOp(self, other)
            assertThat(result, equalTo(expected))
        }
    }
}