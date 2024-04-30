package io.hamal.lib.kua.state

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.numberGet
import io.hamal.lib.kua.type
import io.hamal.lib.value.ValueNumber
import io.hamal.lib.value.ValueString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class ReferenceReleaseTest : StateBaseTest() {

    @TestFactory
    fun `Releases acquired reference`() = runTest { testInstance ->
        testInstance.stringPush(ValueString("a secret I better forget soon"))

        val reference = testInstance.referenceAcquire()
        assertThat(testInstance.topGet(), equalTo(StackTop(0)))

        testInstance.referenceRelease(reference)

        testInstance.referencePush(reference)
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
        assertThat(testInstance.type(1), equalTo(ValueNumber::class))
        assertThat(testInstance.numberGet(1), equalTo(ValueNumber(0.0)))
    }

}