package io.hamal.bootstrap

import io.hamal.lib.kua.ExitError
import io.hamal.lib.kua.extension.ScriptExtension
import io.hamal.lib.kua.extension.ScriptExtensionFactory
import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.Function1In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.value.NumberValue
import io.hamal.lib.kua.value.StringValue
import org.junit.jupiter.api.fail
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class TestExtensionFactory : ScriptExtensionFactory {
    override fun create(): ScriptExtension {
        return ScriptExtension(
            name = "test",
            init = """
                function create_extension_factory()
                    local internal = _internal
                    return function()
                        return {
                            complete = function()
                                return internal.complete()
                            end,
                            fail = function()
                                return internal.fail()
                            end
                        }
                    end
                end
            """.trimIndent(),
            internals = mapOf(
                "complete" to CompleteTestFunction,
                "fail" to FailTestFunction
            )
        )
    }
}

internal object CompleteTestFunction : Function0In0Out() {
    override fun invoke(ctx: FunctionContext) {
        ActiveTest.completeTest()
        throw ExitError(NumberValue.Zero)
    }
}

internal object FailTestFunction : Function1In0Out<StringValue>(
    FunctionInput1Schema(StringValue::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringValue) {
        ActiveTest.failTest(arg1)
        throw ExitError(NumberValue.One)
    }
}

object ActiveTest {
    private val lock = ReentrantLock()
    private val condition = lock.newCondition()
    private var failureReason: String? = null
    fun completeTest() {
        lock.withLock {
            condition.signal()
        }
    }

    fun failTest(cause: StringValue) {
        lock.withLock {
            condition.signal()
            failureReason = cause.value
        }
    }

    fun awaitCompletion() {
        lock.withLock {
            failureReason = null
            condition.await()
            if (failureReason != null) {
                fail(failureReason)
            }
        }
    }
}