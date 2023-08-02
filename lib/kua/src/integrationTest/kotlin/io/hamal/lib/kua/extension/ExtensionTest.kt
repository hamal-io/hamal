package io.hamal.lib.kua.extension

import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.FunctionContext
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import java.nio.file.Files.readAllBytes
import kotlin.io.path.Path

internal class ExtensionTest {

    @Test
    fun `Invokes function of test extension`() {
        sandbox.load(
            """
            local test_extension = require('test/extension')
            for x=1,10 do
                test_extension.call()
            end
        """.trimIndent()
        )
        assertThat(TestCall0In0OutFunction.counter, equalTo(10))
    }

    @Test
    fun `Able to access fields of extension`() {
        sandbox.load(
            """
            local test_extension = require('test/extension')
            assert( test_extension.some_number == 42 )
            assert( test_extension.some_boolean == true)
        """.trimIndent()
        )
    }

    private val sandbox = run {
        NativeLoader.load(Resources)
        Sandbox().also { sb ->
            sb.register(
                ScriptExtension(
                    name = "test/extension",
                    init = String(readAllBytes(Path("src/integrationTest/resources/test-extension.lua"))),
                    internals = mapOf(
                        "test_extension_call" to TestCall0In0OutFunction
                    )
                )
            )
        }
    }
}

internal object TestCall0In0OutFunction : Function0In0Out() {
    override fun invoke(ctx: FunctionContext) {
        counter++
    }

    var counter = 0
}