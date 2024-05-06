package io.hamal.extension.std.decimal

import io.hamal.runner.test.RunnerFixture.unitOfWork
import org.junit.jupiter.api.Test

internal class MinTest : AbstractTest() {
    @Test
    fun `decimal, number`() {
        runTest(
            unitOfWork(
                """
                local decimal = require('std.decimal').create()
                local x = decimal.new('512')
                local y = 1024

                assert(decimal.min(x,x) == x)
                assert(decimal.min(x,y) == x)
            """
            )
        )
    }


    @Test
    fun `decimal, decimal`() {
        runTest(
            unitOfWork(
                """
                local decimal = require('std.decimal').create()
                local x = decimal.new('512')
                local y = decimal.new(1024)

                assert(decimal.min(x,x) == x)
                assert(decimal.min(x,y) == x)
            """
            )
        )
    }

    @Test
    fun `number, decimal`() {
        runTest(
            unitOfWork(
                """
              local decimal = require('std.decimal').create()
                local x = 512
                local y = decimal.new(1024)

                assert(decimal.min(x,x) == decimal.new(512))
                assert(decimal.min(x,y) == decimal.new(512))
            """
            )
        )
    }
}
