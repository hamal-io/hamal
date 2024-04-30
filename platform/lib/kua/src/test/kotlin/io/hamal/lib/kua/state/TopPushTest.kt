package io.hamal.lib.kua.state

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.errorGet
import io.hamal.lib.kua.topPush
import io.hamal.lib.kua.type
import io.hamal.lib.value.ValueDecimal
import io.hamal.lib.value.ValueError
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class TopPushTest : StateBaseTest() {

    @TestFactory
    fun `Pushes a copy of the value on top of the stack`() = runTest { testInstance ->
        testInstance.errorPush(ValueError("test"))
        testInstance.decimalPush(ValueDecimal(1))
        testInstance.decimalPush(ValueDecimal(2))
        testInstance.decimalPush(ValueDecimal(3))

        testInstance.topPush(1)
        assertThat(testInstance.topGet(), equalTo(StackTop(5)))
        assertThat(testInstance.type(-1), equalTo(ValueError::class))

        assertThat(testInstance.errorGet(5), equalTo(ValueError("test")))
    }
}