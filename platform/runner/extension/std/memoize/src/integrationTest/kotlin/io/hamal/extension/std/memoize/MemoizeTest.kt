package io.hamal.extension.std.memoize

import io.hamal.runner.test.RunnerFixture.unitOfWork
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class MemoizeTest : AbstractTest() {
    @Test
    fun `Memoize function `() {
        runTest(
            unitOfWork(
                """
                    test = require_plugin('test')
                
                    function fest_instance() test.invoke() end
                    memoize = require('std.memoize').create(fest_instance)
                
                    for i = 1, 100 do memoize() end
            """
            ),

        )

        assertThat(InvokeFunction.invocations, equalTo(1))
    }



}