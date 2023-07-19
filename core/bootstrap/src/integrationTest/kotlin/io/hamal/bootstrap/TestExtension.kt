package io.hamal.bootstrap

import io.hamal.agent.extension.api.Extension
import io.hamal.lib.kua.Bridge
import io.hamal.lib.kua.value.FuncValue
import io.hamal.lib.kua.value.ModuleValue
import org.junit.jupiter.api.fail
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class TestExtension : Extension {
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
    override fun create(): ModuleValue {
        TODO("Not yet implemented")
    }

}


internal class CompleteTest : FuncValue() {
    //    override fun invoke(ctx: FuncContext): Value {
//        ActiveTest.completeTest()
//        throw ExitException(NumberValue.Zero)
//    }
    override fun invokedByLua(state: Bridge): Int {
        TODO("Not yet implemented")
    }
}

internal class FailTest : FuncValue() {
//    override fun invoke(ctx: FuncContext): Value {
//        val reason = ctx.params.firstOrNull()?.value
//            ?.let { value -> if (value is StringValue) value.value else null }
//            ?: "Failed"
//
//        ActiveTest.failTest(reason)
//        throw ExitException(NumberValue.One)
//    }
//
    override fun invokedByLua(state: Bridge): Int {
        TODO("Not yet implemented")
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

    fun failTest(reason: String) {
        lock.withLock {
            condition.signal()
            failureReason = reason
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