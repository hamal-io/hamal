package io.hamal.bootstrap

import io.hamal.agent.extension.api.Extension
import io.hamal.lib.script.api.value.*
import io.hamal.lib.script.impl.ExitException
import org.junit.jupiter.api.fail
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class TestExtension : Extension {
    override fun create(): EnvValue {
        return EnvValue(
            ident = IdentValue("test"),
            values = TableValue(
                "assert" to TestAssert(),
                "complete" to CompleteTest(),
                "fail" to FailTest()
            )
        )
    }

}

internal class TestAssert : FuncValue() {
    override fun invoke(ctx: FuncContext): Value {
        val parameters = ctx.params

        val line = ctx.params.first().expression.position.line

        val assertionMessage = ctx.params.getOrNull(1)
            ?.value
            ?.let { (it as StringValue).value }
            ?: "${ctx.params.first().expression}"

        val result = parameters.firstOrNull()?.value
        if (result != TrueValue) {
            if (result != FalseValue) {
                ActiveTest.failTest("Line $line: Assertion of non boolean value is always false")
                throw ExitException(NumberValue.One)
            }
            ActiveTest.failTest("Line $line: Assertion violated: '$assertionMessage'")
            throw ExitException(NumberValue.One)
        }
        return NilValue
    }
}

internal class CompleteTest : FuncValue() {
    override fun invoke(ctx: FuncContext): Value {
        ActiveTest.completeTest()
        throw ExitException(NumberValue.Zero)
    }
}

internal class FailTest : FuncValue() {
    override fun invoke(ctx: FuncContext): Value {
        val reason = ctx.params.firstOrNull()?.value
            ?.let { value -> if (value is StringValue) value.value else null }
            ?: "Failed"

        ActiveTest.failTest(reason)
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