package io.hamal.extension.std.algo

import io.hamal.runner.test.RunnerFixture.unitOfWork
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal object ListTest : AbstractTest() {

    @BeforeEach
    fun setup() {
        InvokeFunction.invocations = 0
    }

    @Test
    fun `Nested functions which should be used only once`() {
        runTest(
            unitOfWork(
                """
               print('hello world') 
            """
            )
        )

    }

}