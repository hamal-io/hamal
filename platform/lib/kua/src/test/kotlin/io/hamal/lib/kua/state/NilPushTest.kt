package io.hamal.lib.kua.state

import io.hamal.lib.kua.type.KuaNil
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class NilPushTest : StateBaseTest() {

    @TestFactory
    fun `Pushes value on stack`() = runTest { testInstance ->
        val result = testInstance.nilPush()
        assertThat(result, equalTo(1))
        assertThat(testInstance.topGet(), equalTo(1))
        assertThat(testInstance.type(1), equalTo(KuaNil::class))
    }

}