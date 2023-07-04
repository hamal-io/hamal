package io.hamal.bootstrap

import io.hamal.agent.extension.api.Extension
import io.hamal.agent.extension.api.ExtensionFunc
import io.hamal.agent.extension.api.ExtensionFuncInvocationContext
import io.hamal.lib.script.api.value.*
import io.hamal.lib.script.impl.ExitException
import org.junit.jupiter.api.fail
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class TestExtension : Extension {
    override fun create(): EnvValue {
        return EnvValue(
            ident = IdentValue("test"),
            values = mapOf(
                IdentValue("assert") to TestAssert(),
                IdentValue("complete") to CompleteTest(),
                IdentValue("fail") to FailTest()
            )
        )
    }

}

internal class TestAssert : ExtensionFunc() {
    override fun invoke(ctx: ExtensionFuncInvocationContext): Value {
        val parameters = ctx.parameters
        val assertionMessage = "TBD"

        val result = parameters.firstOrNull()
        if (result != TrueValue) {
            if (result != FalseValue) {
                ActiveTest.failTest("Assertion of non boolean value is always false")
                throw ExitException(NumberValue.One)
            }
            ActiveTest.failTest("Assertion violated: '$assertionMessage'")
            throw ExitException(NumberValue.One)
        }
        return NilValue
    }
}

internal class CompleteTest : ExtensionFunc() {
    override fun invoke(ctx: ExtensionFuncInvocationContext): Value {
        ActiveTest.completeTest()
        throw ExitException(NumberValue.Zero)
    }
}

internal class FailTest : ExtensionFunc() {
    override fun invoke(ctx: ExtensionFuncInvocationContext): Value {
        val reason = ctx.parameters.firstOrNull()
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