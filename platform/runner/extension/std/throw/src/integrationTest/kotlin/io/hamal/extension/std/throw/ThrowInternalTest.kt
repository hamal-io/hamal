package io.hamal.extension.std.`throw`

import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.domain.vo.ExecResult
import io.hamal.lib.domain.vo.ExecStatusCode.Companion.ExecStatusCode
import io.hamal.runner.test.RunnerFixture.unitOfWork
import io.hamal.runner.test.TestFailConnector
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class ThrowInternalTest : AbstractTest() {
    @Test
    fun `Throws internal server `() {
        runTest(
            unitOfWork(
                """
                throw = require('std.throw').create()
                
                throw.internal('something went wrong')
            """
            ),
            TestFailConnector { _, statusCode, result ->
                assertThat(statusCode, equalTo(ExecStatusCode(500)))
                assertThat(result, equalTo(ExecResult(ValueObject.builder().set("message", "something went wrong").build())))
            }
        )
    }
}