package io.hamal.extension.std.decimal

import io.hamal.runner.test.RunnerFixture.unitOfWork
import org.junit.jupiter.api.Test

internal class MaxTest : AbstractTest() {
    @Test
    fun `decimal, number`() {
        runTest(
            unitOfWork(
                """
                local decimal = require('std.decimal').create()
                local x = decimal.new('512')
                local y = 1024

                assert(decimal.max(x,x) == x)
                assert(decimal.max(x,y) == decimal.new(1024))
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

                assert(decimal.max(x,x) == x)
                assert(decimal.max(x,y) == y)
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

                assert(decimal.max(x,x) == decimal.new(512))
                assert(decimal.max(x,y) == y)
            """
            )
        )
    }
}
