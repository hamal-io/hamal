package io.hamal.extension.std.decimal

import AbstractCapabilityTest
import io.hamal.extension.std.log.DecimalCapabilityFactory
import org.junit.jupiter.api.Test

internal class AddTest : AbstractCapabilityTest() {
    @Test
    fun `decimal, number`() {
        val execute = createTestExecutor(DecimalCapabilityFactory)
        execute.run(
            unitOfWork(
                """
                local decimal = require('decimal')
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
        val execute = createTestExecutor(DecimalCapabilityFactory)
        execute.run(
            unitOfWork(
                """
                local decimal = require('decimal')
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
        val execute = createTestExecutor(DecimalCapabilityFactory)
        execute.run(
            unitOfWork(
                """
                local decimal = require('decimal')
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
