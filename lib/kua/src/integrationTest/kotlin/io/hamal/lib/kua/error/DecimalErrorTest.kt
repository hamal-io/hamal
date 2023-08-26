package io.hamal.lib.kua.error

import io.hamal.lib.kua.DecimalError
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NopSandboxContext
import io.hamal.lib.kua.Sandbox
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DecimalErrorTest {

    @Test
    fun `Throws an error if decimal error occurs`() {
        val error = assertThrows<DecimalError> {
            sandbox.load(
                """
                local b = __decimal__.new(123)
                print(b / 0)
            """.trimIndent()
            )
        }
        assertThat(error.message, equalTo("Division_by_zero"))
    }

    private val sandbox = run {
        NativeLoader.load(NativeLoader.Preference.Resources)
        Sandbox(NopSandboxContext())
    }
}