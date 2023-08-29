package io.hamal.lib.kua

import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.extension.NativeExtension
import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.type.CodeType
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test


internal class RegisterExtensionTest : BaseSandboxTest() {
    @Test
    fun `Register a module and call function`() {
        class TestFunction : Function0In0Out() {
            override fun invoke(ctx: FunctionContext) {
                set = true
            }

            var set = false
        }

        val func = TestFunction()
        testInstance.register(
            NativeExtension(
                name = "secret_module",
                values = mapOf("magic" to func)
            )
        )

        testInstance.load(
            CodeType("""secret_module.magic()""")
        )
        assertThat(func.set, equalTo(true))
    }
}


internal sealed class BaseSandboxTest {
    val testInstance = run {
        NativeLoader.load(Resources)
        Sandbox(NopSandboxContext())
    }

    fun verifyStackIsEmpty() {
        assertThat("Stack is empty", testInstance.top, equalTo(0))
    }
}
