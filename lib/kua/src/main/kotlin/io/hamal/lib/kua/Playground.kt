package io.hamal.lib.kua

import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.NamedFunctionValue


fun main() {
    FixedPathLoader.load()

    Sandbox().use { sb ->
        val neverCalled = object : Function0In0Out() {
            override fun invoke(ctx: FunctionContext) {
                called = true
            }

            var called = false
        }
        val throwError = object : Function0In0Out() {
            override fun invoke(ctx: FunctionContext) {
                throw IllegalArgumentException("some illegal argument")
            }
        }
        sb.register(
            Extension(
                name = "test",
                functions = listOf(
                    NamedFunctionValue("throw_error", throwError),
                    NamedFunctionValue("never_called", neverCalled),
                )
            )
        )

        try {
            sb.runCode(
                """
                test.throw_error()
                test.never_called()
            """
            )
        } catch (t: Throwable) {
            t.printStackTrace()
        }

        println("Never called: ${neverCalled.called}")
    }
}


