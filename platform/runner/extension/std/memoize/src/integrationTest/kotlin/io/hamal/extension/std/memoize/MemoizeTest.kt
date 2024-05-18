package io.hamal.extension.std.memoize

import io.hamal.lib.common.value.ValueString
import io.hamal.lib.domain.vo.ExecStatusCode.Companion.ExecStatusCode
import io.hamal.runner.test.RunnerFixture.unitOfWork
import io.hamal.runner.test.TestFailConnector
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class MemoizeTest : AbstractTest() {

    @BeforeEach
    fun before() {
        InvokeFunction.invocations = 0
    }

    @Test
    fun `Throws if try to memoize nil`() {
        runTest(
            unitOfWork(
                """
                memoize = require('std.memoize').create()
                fn = memoize(nil)
            """
            ),
            connector = TestFailConnector { _, statusCode, error ->
                assertThat(statusCode, equalTo(ExecStatusCode(400)))
                assertThat(
                    error.getString("message"),
                    equalTo(ValueString("Only functions and callable tables are memoizable"))
                )
            }
        )
    }

    @Test
    fun `Memoize function `() {
        runTest(
            unitOfWork(
                """
                    test = require_plugin('test')
                
                    function test_instance() test.invoke() end
                    memoize = require('std.memoize').create()
                    fn = memoize(test_instance)
                
                    for i = 1, 100 do fn() end
            """
            )
        )

        assertThat(InvokeFunction.invocations, equalTo(1))
    }

    @Test
    fun `Memoize different parameter `() {
        runTest(
            unitOfWork(
                """
                    test = require_plugin('test')
                
                    function test_instance(value) test.invoke(); return value * value end
                    memoize = require('std.memoize').create()
                    fn = memoize(test_instance)
                
                    for i = 1, 100 do fn(2) end
                    for i = 1, 100 do fn(4) end
                    for i = 1, 100 do fn(8) end
            """
            )
        )

        assertThat(InvokeFunction.invocations, equalTo(3))
    }

}