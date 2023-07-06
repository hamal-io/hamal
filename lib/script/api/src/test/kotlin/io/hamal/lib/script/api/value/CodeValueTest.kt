package io.hamal.lib.script.api.value

import io.hamal.lib.domain.Tuple4
import io.hamal.lib.script.api.value.ValueSerializationFixture.generateTestCases
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class CodeValueTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(
            CodeValue("log.info('hamal rocks')"),
            """{"type":"CodeValue","value":"log.info('hamal rocks')"}"""
        ),
    ).flatten()
}

class DefaultCodeValueMetaTableTest {
    @Test
    fun `Every operation is covered`() {
        assertThat(DefaultCodeValueMetaTable.operators, hasSize(2))
    }

    @Test
    fun `Test type`() {
        assertThat(DefaultCodeValueMetaTable.type, equalTo("code"))
    }

    @TestFactory
    fun infix() = listOf<Tuple4<CodeValue, ValueOperator.Type, Value, Value>>(
        Tuple4(CodeValue("40 + 2"), ValueOperator.Type.Eq, CodeValue("40 + 2"), TrueValue),
        Tuple4(CodeValue("40 + 2"), ValueOperator.Type.Eq, CodeValue("1340 -3"), FalseValue),
        Tuple4(CodeValue("40 + 2"), ValueOperator.Type.Neq, CodeValue("40 + 2"), FalseValue),
        Tuple4(CodeValue("40 + 2"), ValueOperator.Type.Neq, CodeValue("1340 - 3"), TrueValue),
    ).map { (self, operator, other, expected) ->
        DynamicTest.dynamicTest("$self $operator $other = $expected") {
            val infixOp = self.findInfixOperation(operator, other.type())!!
            val result = infixOp(self, other)
            assertThat(result, equalTo(expected))
        }
    }
}