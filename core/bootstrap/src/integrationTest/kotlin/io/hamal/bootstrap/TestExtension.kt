package io.hamal.bootstrap

import io.hamal.lib.kua.ExitError
import io.hamal.lib.kua.extension.NativeExtension
import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.Function1In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.value.NumberValue
import io.hamal.lib.kua.value.StringValue
import org.junit.jupiter.api.fail
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class TestExtensionFactory {
    fun create(): NativeExtension {
        return NativeExtension(
            name = "test", //FIXME becomes VO
            values = mapOf(
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

    fun failTest(reason: StringValue) {
        lock.withLock {
            condition.signal()
            failureReason = reason.value
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