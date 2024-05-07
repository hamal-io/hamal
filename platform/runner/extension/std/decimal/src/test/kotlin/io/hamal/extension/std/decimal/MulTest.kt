package io.hamal.extension.std.decimal

import io.hamal.runner.test.RunnerFixture.unitOfWork
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class MulTest : AbstractTest() {
    @Test
    fun `decimal, number`() {
        runTest(
            unitOfWork(
                """
                local decimal = require('std.decimal').create()
                local x = decimal.new('3.14')
                local y = 2
                local result = x * y
                assert(type(result) == 'decimal')
                assert(decimal.to_string(result) == '6.28')
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
                local x = decimal.new('3.14')
                local y = decimal.new(2)
                local result = x * y
                assert(type(result) == 'decimal')
                assert(decimal.to_string(result) == '6.28')
            """
            )
        )
    }

    @Disabled //204-breaks all tests
    @Test
    fun `number, decimal`() {
        runTest(
            unitOfWork(
                """
                local decimal = require('std.decimal').create()
                local x = 3.14
                local y = decimal.new(2)
                local result = x * y
                assert(type(result) == 'decimal')
                assert(decimal.to_string(result) == '6.28')
            """
            )
        )
    }
}
