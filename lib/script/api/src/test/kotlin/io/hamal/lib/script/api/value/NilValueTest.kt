package io.hamal.lib.script.api.value

import io.hamal.lib.domain.Tuple4
import io.hamal.lib.kua.value.ValueOperator.Type
import io.hamal.lib.kua.value.ValueOperator.Type.Eq
import io.hamal.lib.kua.value.ValueOperator.Type.Neq
import io.hamal.lib.kua.value.ValueSerializationFixture.generateTestCases
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class NilValueTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(NilValue, """{"type":"NilValue"}"""),
    ).flatten()
}

class DefaultNilValueMetaTableTest {
    @Test
    fun `Every operation is covered`() {
        assertThat(DefaultNilValueMetaTable.operators, hasSize(4))
    }

    @Test
    fun `Test type`() {
        assertThat(DefaultNilValueMetaTable.type, equalTo("nil"))
    }

    @TestFactory
    fun infix() = listOf<Tuple4<NilValue, Type, Value, Value>>(

        Tuple4(NilValue, Eq, NilValue, TrueValue),
        Tuple4(NilValue, Neq, NilValue, FalseValue),

        Tuple4(NilValue, Eq, NumberValue(1), FalseValue),
        Tuple4(NilValue, Neq, NumberValue(1), TrueValue),


        ).map { (self, operator, other, expected) ->
        DynamicTest.dynamicTest("$self $operator $other = $expected") {
            val infixOp = self.findInfixOperation(operator, other.type())!!
            val result = infixOp(self, other)
            assertThat(result, equalTo(expected))
        }
    }
}