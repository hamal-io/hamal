package io.hamal.lib.kua.state

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.type.KuaNumber
import io.hamal.lib.kua.type.KuaString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class ReferenceReleaseTest : StateBaseTest() {

    @TestFactory
    fun `Releases acquired reference`() = runTest { testInstance ->
        testInstance.stringPush(KuaString("a secret I better forget soon"))

        val reference = testInstance.referenceAcquire()
        assertThat(testInstance.topGet(), equalTo(StackTop(0)))

        testInstance.referenceRelease(reference)

        testInstance.referencePush(reference)
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
        assertThat(testInstance.type(1), equalTo(KuaNumber::class))
        assertThat(testInstance.numberGet(1), equalTo(KuaNumber(0.0)))
    }

}