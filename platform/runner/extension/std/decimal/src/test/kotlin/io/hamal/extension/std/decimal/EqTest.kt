package io.hamal.extension.std.decimal

import io.hamal.runner.test.RunnerFixture.unitOfWork
import org.junit.jupiter.api.Test

internal class EqTest : AbstractTest() {
    @Test
    fun `decimal, number`() {
        runTest(
            unitOfWork(
                """
                local decimal = require('std.decimal').create()
                local x = decimal.new('512')
                local y = 1024
                local z = 512
                
                assert(x ~= y)
                assert(x ~= z)
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
                local z = decimal.new(512)
                
                assert(x ~= y)
                assert(x == z)
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
                local z = decimal.new(512)
                
                assert(x ~= y)
                assert(x ~= z)
            """
            )
        )
    }
}
