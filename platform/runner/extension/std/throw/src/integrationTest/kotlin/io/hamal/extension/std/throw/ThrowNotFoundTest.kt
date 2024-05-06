package io.hamal.extension.std.`throw`

import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.domain.vo.ExecResult
import io.hamal.lib.domain.vo.ExecStatusCode.Companion.ExecStatusCode
import io.hamal.runner.test.AbstractRunnerTest
import io.hamal.runner.test.TestFailConnector
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class ThrowNotFoundTest : AbstractRunnerTest() {
    @Test
    fun `Throws not found error `() {
        createTestRunner(
            extensionFactories = listOf(ExtensionStdThrowFactory),
            connector = TestFailConnector { _, statusCode, result ->
                assertThat(statusCode, equalTo(ExecStatusCode(404)))
                assertThat(result, equalTo(ExecResult(ValueObject.builder().set("message", "could not find xyz").build())))
            }
        ).also { runner ->
            runner.run(
                unitOfWork(
                    """
                throw = require('std.throw').create()
                
                throw.not_found('could not find xyz')
            """
                )
            )
        }
    }
}