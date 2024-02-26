package io.hamal.lib.kua.native

import io.hamal.lib.kua.Native
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NopSandboxContext
import io.hamal.lib.kua.Sandbox
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal abstract class NativeBaseTest {

    val testInstance: Native = run {
        NativeLoader.load(NativeLoader.Preference.Resources)
        Native(Sandbox(NopSandboxContext()))
    }

    fun verifyStackIsEmpty() {
        assertThat("Stack is empty", testInstance.topGet(), equalTo(0))
    }
}
