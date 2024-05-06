package io.hamal.extension.std.`throw`

import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.domain.vo.ExecResult
import io.hamal.lib.domain.vo.ExecStatusCode.Companion.ExecStatusCode
import io.hamal.runner.test.AbstractRunnerTest
import io.hamal.runner.test.TestFailConnector
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class ThrowInternalTest : AbstractRunnerTest() {
    @Test
    fun `Throws internal server `() {
        createTestRunner(
            extensionFactories = listOf(ExtensionStdThrowFactory),
            connector = TestFailConnector { _, statusCode, result ->
                assertThat(statusCode, equalTo(ExecStatusCode(500)))
                assertThat(result, equalTo(ExecResult(ValueObject.builder().set("message", "something went wrong").build())))
            }
        ).also { runner ->
            runner.run(
                unitOfWork(
                    """
                throw = require('std.throw').create()
                
                throw.internal('something went wrong')
            """
                )
            )
        }
    }
}