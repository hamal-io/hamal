package io.hamal.lib.kua

import io.hamal.lib.kua.value.CodeValue
import io.hamal.lib.kua.value.FuncValue
import io.hamal.lib.kua.value.ModuleValue
import io.hamal.lib.kua.value.NamedFuncValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test


internal class RegisterModuleTest : BaseSandboxTest() {
    @Test
    fun `Register a module and call function`() {
        class TestFunc : FuncValue() {
            override fun invokedByLua(bridge: Bridge): Int {
                set = true
                return 0
            }

            var set = false
        }

        val func = TestFunc()
        testInstance.register(
            ModuleValue(
                name = "secret_module",
                namedFuncs = listOf(
                    NamedFuncValue("magic", func)
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
    val testInstance = Sandbox(ResourceLoader)

    fun verifyStackIsEmpty() {
        assertThat("Stack is empty", testInstance.stack.size(), equalTo(0))
    }
}
