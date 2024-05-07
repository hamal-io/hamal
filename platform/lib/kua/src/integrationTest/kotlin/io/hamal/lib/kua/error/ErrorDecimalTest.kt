package io.hamal.lib.kua.error

import io.hamal.lib.kua.ErrorDecimal
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.SandboxContextNop
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.common.value.ValueCode
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ErrorDecimalTest {

    @Test
    fun `Throws an error if decimal error occurs`() {
        val error = assertThrows<ErrorDecimal> {
            sandbox.codeLoad(
                ValueCode(
                    """
                local b = __decimal__.new(123)
                print(b / 0)
            """.trimIndent()
                )
            )
        }
        assertThat(error.message, equalTo("Division_by_zero"))
    }

    private val sandbox = run {
        NativeLoader.load(NativeLoader.Preference.Resources)
        Sandbox(SandboxContextNop)
    }
}