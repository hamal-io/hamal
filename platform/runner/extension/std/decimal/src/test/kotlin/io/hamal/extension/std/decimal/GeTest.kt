package io.hamal.extension.std.decimal

import io.hamal.runner.test.RunnerFixture.unitOfWork
import org.junit.jupiter.api.Test

internal class GeTest : AbstractTest() {
    @Test
    fun `decimal, number`() {
        runTest(
            unitOfWork(
                """
                local decimal = require('std.decimal').create()
                local a = decimal.new('512')
                local b = 1024
                local c = 512
                local d = 256
                
                assert( (a >= b) == false )
                assert( (a >= c) == true )
                assert( (a >= d) == true )
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
                local a = decimal.new('512')
                local b = decimal.new(1024)
                local c = decimal.new(512)
                local d = decimal.new(256)
                
                assert( (a >= b) == false )
                assert( (a >= c) == true )
                assert( (a >= d) == true )
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
                local a = 512
                local b = decimal.new(1024)
                local c = decimal.new(512)
                local d = decimal.new(256)
                
                assert( (a >= b) == false )
                assert( (a >= c) == true )
                assert( (a >= d) == true )
            """
            )
        )
    }
}
