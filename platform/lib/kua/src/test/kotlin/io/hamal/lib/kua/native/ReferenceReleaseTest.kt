package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class ReferenceReleaseTest : NativeBaseTest() {

    @Test
    fun `Releases acquired reference`() {
        testInstance.stringPush("a secret I better forget soon")

        val reference: Int = testInstance.referenceAcquire()
        assertThat(testInstance.topGet(), equalTo(0))

        testInstance.referenceRelease(reference)

        testInstance.referencePush(reference)
        assertThat(testInstance.topGet(), equalTo(1))
        assertThat(testInstance.type(1), equalTo(3))
        assertThat(testInstance.numberGet(1), equalTo(0.0))
    }

}