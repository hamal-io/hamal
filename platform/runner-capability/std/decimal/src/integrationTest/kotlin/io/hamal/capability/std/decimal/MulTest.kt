package io.hamal.capability.std.decimal

import AbstractCapabilityTest
import io.hamal.capability.std.log.DecimalCapabilityFactory
import org.junit.jupiter.api.Test

internal class MulTest : AbstractCapabilityTest() {
    @Test
    fun `decimal, number`() {
        val execute = createTestExecutor(DecimalCapabilityFactory)
        execute.run(
            unitOfWork(
                """
                local decimal = require('decimal')
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
        val execute = createTestExecutor(DecimalCapabilityFactory)
        execute.run(
            unitOfWork(
                """
                local decimal = require('decimal')
                local x = decimal.new('3.14')
                local y = decimal.new(2)
                local result = x * y
                assert(type(result) == 'decimal')
                assert(decimal.to_string(result) == '6.28')
            """
            )
        )
    }

    @Test
    fun `number, decimal`() {
        val execute = createTestExecutor(DecimalCapabilityFactory)
        execute.run(
            unitOfWork(
                """
                local decimal = require('decimal')
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
