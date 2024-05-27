package io.hamal.extension.std.once

import io.hamal.lib.common.value.ValueString
import io.hamal.lib.domain.vo.ExecStatusCode.Companion.ExecStatusCode
import io.hamal.runner.test.RunnerFixture.unitOfWork
import io.hamal.runner.test.TestFailConnector
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class OnceTest : AbstractTest() {

    @BeforeEach
    fun before() {
        InvokeFunction.invocations = 0
    }

    @Test
    fun `Use a function once`() {
        runTest(
            unitOfWork(
                """
                    test = require_plugin('test')

                    function test_instance() test.invoke() end
                    once = require('std.once').create()
                    fn = once(test_instance)

                    for i = 1, 100 do fn() end
            """
            )
        )

        assertThat(InvokeFunction.invocations, equalTo(1))
    }


    @Test
    fun `Nested functions which should be used only once`() {
        runTest(
            unitOfWork(
                """
                    test = require_plugin('test')
                    once = require('std.once').create()
                    
                    fn_1 = once(function() test.invoke() end)
                    fn_2 = once(function() fn_1()  end)
                    fn_3 = once(function() fn_1(); fn_2() end)

                    for i = 1, 100 do fn_1() end
                    for i = 1, 100 do fn_2() end
                    for i = 1, 100 do fn_3() end
            """
            )
        )

        assertThat(InvokeFunction.invocations, equalTo(1))
    }

    @Test
    fun `Throws if try to once nil`() {
        runTest(
            unitOfWork(
                """
                once = require('std.once').create()()
                print(dump(once))
                fn = once(nil)
            """
            ),
            connector = TestFailConnector { _, statusCode, error ->
                assertThat(statusCode, equalTo(ExecStatusCode(400)))
                assertThat(
                    error.getString("message"),
                    equalTo(ValueString("Only functions and callable tables can be used once"))
                )
            }
        )
    }

    @Test
    fun `Tries to call once function with parameter`() {
        runTest(
            unitOfWork(
                """
                test = require_plugin('test')
                function test_instance() test.invoke() end
                once = require('std.once').create()
                fn = once(test_instance)
                for i = 1, 100 do fn(1,2,3) end
            """
            ),
            connector = TestFailConnector { _, statusCode, error ->
                assertThat(statusCode, equalTo(ExecStatusCode(400)))
                assertThat(
                    error.getString("message"),
                    equalTo(ValueString("Once does not accept any parameter"))
                )
            }
        )
    }

}