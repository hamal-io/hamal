package io.hamal.extension.std.error

import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.domain.vo.ExecResult
import io.hamal.lib.domain.vo.ExecStatusCode.Companion.ExecStatusCode
import io.hamal.runner.test.AbstractRunnerTest
import io.hamal.runner.test.TestFailConnector
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class ErrorInternalTest : AbstractRunnerTest() {
    @Test
    fun `Throws internal server error `() {
        createTestRunner(
            extensionFactories = listOf(ExtensionStdErrorFactory),
            connector = TestFailConnector { _, statusCode, result ->
                assertThat(statusCode, equalTo(ExecStatusCode(500)))
                assertThat(result, equalTo(ExecResult(ValueObject.builder().set("message", "something went wrong").build())))
            }
        ).also { runner ->
            runner.run(
                unitOfWork(
                    """
                error = require('std.error').create()
                
                error.internal('something went wrong')
            """
                )
            )
        }
    }
}