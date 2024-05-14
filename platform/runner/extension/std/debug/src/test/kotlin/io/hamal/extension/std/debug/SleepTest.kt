package io.hamal.extension.std.debug

import io.hamal.runner.test.RunnerFixture.unitOfWork
import org.junit.jupiter.api.Test

internal class SleepTest : AbstractTest() {
    @Test
    fun `Timeout test`() {
        runTest(
            unitOfWork(
                """
                debug = require('std.debug').create()
                
                local count = 0
                while count <= 5 do
                    debug.sleep(10)
                    count = count + 1
                end
                
                assert( count == 6 )
            """
            )
        )

    }


}

