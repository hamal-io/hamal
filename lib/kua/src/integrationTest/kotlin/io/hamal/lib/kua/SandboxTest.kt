package io.hamal.lib.kua

import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.value.CodeValue
import io.hamal.lib.kua.value.ExtensionValue
import io.hamal.lib.kua.value.Function0In0Out
import io.hamal.lib.kua.value.NamedFunctionValue
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
            ExtensionValue(
                name = "secret_module",
                functions = listOf(
                    NamedFunctionValue("magic", func)
                )
            )
        )

        testInstance.runCode(
            CodeValue("""secret_module.magic()""")
        )
        assertThat(func.set, equalTo(true))
    }
}


internal sealed class BaseSandboxTest {
    val testInstance = run {
        ResourceLoader.load()
        Sandbox()
    }

    fun verifyStackIsEmpty() {
        assertThat("Stack is empty", testInstance.stack.size(), equalTo(0))
    }
}
