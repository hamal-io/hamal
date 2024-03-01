package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class AbsIndexTest : NativeBaseTest() {

    @Test
    fun `abs of positive index`() {
        testInstance.numberPush(1.0)
        testInstance.numberPush(2.0)
        testInstance.numberPush(3.0)
        testInstance.numberPush(4.0)

        val result = testInstance.absIndex(3)
        assertThat(result, equalTo(3))
    }

    @Test
    fun `abs of negative index`() {
        testInstance.numberPush(1.0)
        testInstance.numberPush(2.0)
        testInstance.numberPush(3.0)
        testInstance.numberPush(4.0)

        val result = testInstance.absIndex(-2)
        assertThat(result, equalTo(3))
    }
}
