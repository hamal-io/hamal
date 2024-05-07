package io.hamal.extension.std.decimal

import io.hamal.runner.test.RunnerFixture.unitOfWork
import org.junit.jupiter.api.Test

internal class AddTest : AbstractTest() {
    @Test
    fun `decimal, number`() {
        runTest(
            unitOfWork(
                """
                local decimal = require('std.decimal').create()
                local x = decimal.new('512')
                local y = 1024
                local result = x + y
                assert(type(result) == 'decimal')
                assert(decimal.to_string(result) == '1536')
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
                local result = x + y
                assert(type(result) == 'decimal')
                assert(decimal.to_string(result) == '1536')
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
                local result = x + y
                assert(type(result) == 'decimal')
                assert(decimal.to_string(result) == '1536')
            """
            )
        )
    }
}
