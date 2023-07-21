package io.hamal.bootstrap

import io.hamal.lib.kua.value.*
import io.hamal.lib.kua.value.function.FunctionContext
import io.hamal.lib.kua.value.function.FunctionInput1Schema
import org.junit.jupiter.api.fail
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class TestExtension {
    //    override fun create(): EnvValue {
//        return EnvValue(
//            ident = IdentValue("test"),
//            values = TableValue(
//                "assert" to TestAssert(),
//                "complete" to CompleteTest(),
//                "fail" to FailTest()
//            )
//        )
//    }
    fun create(): ExtensionValue {
        return ExtensionValue(
            name = "test", //FIXME becomes VO
            functions = listOf(
                NamedFunctionValue(
                    name = "complete", //FIXME becomes VO
                    function = CompleteTestFunction
                ),
                NamedFunctionValue(
                    name = "fail",
                    function = FailTestFunction
                )
            )
        )
    }
}

class ExitException(status: NumberValue) : Exception(status.toString())


internal object CompleteTestFunction : Function0In0Out() {
    override fun invoke(ctx: FunctionContext) {
        ActiveTest.completeTest()
        throw ExitException(NumberValue.Zero)
    }
}

internal object FailTestFunction : Function1In0Out<StringValue>(
    FunctionInput1Schema(StringValue::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringValue) {
        ActiveTest.failTest(arg1)
        throw ExitException(NumberValue.One)
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