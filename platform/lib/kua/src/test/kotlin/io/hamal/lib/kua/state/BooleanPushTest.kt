package io.hamal.lib.kua.state

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.booleanGet
import io.hamal.lib.value.ValueTrue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class BooleanPushTest : StateBaseTest() {

    @TestFactory
    fun `Pushes value on stack`() = runTest { testInstance ->
        val result = testInstance.booleanPush(ValueTrue)
        assertThat(result, equalTo(StackTop(1)))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
        assertThat(testInstance.booleanGet(1), equalTo(ValueTrue))
    }

}