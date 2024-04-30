package io.hamal.lib.kua.state

import io.hamal.lib.kua.absIndex
import io.hamal.lib.kua.type.KuaNumber
import io.hamal.lib.value.ValueDecimal
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class AbsIndexTest : StateBaseTest() {

    @TestFactory
    fun `abs of positive index`() = runTest { testInstance ->
        testInstance.decimalPush(ValueDecimal(1.0))
        testInstance.decimalPush(ValueDecimal(2.0))
        testInstance.decimalPush(ValueDecimal(3.0))
        testInstance.decimalPush(ValueDecimal(4.0))

        val result = testInstance.absIndex(3)
        assertThat(result, equalTo(KuaNumber(3)))
    }

    @TestFactory
    fun `abs of negative index`() = runTest { testInstance ->
        testInstance.decimalPush(ValueDecimal(1.0))
        testInstance.decimalPush(ValueDecimal(2.0))
        testInstance.decimalPush(ValueDecimal(3.0))
        testInstance.decimalPush(ValueDecimal(4.0))

        val result = testInstance.absIndex(-2)
        assertThat(result, equalTo(KuaNumber(3)))
    }
}