package io.hamal.extension.std.decimal

import AbstractExtensionTest
import io.hamal.extension.std.log.DecimalExtensionFactory
import org.junit.jupiter.api.Test

internal class SubTest : AbstractExtensionTest() {
    @Test
    fun `decimal, number`() {
        val execute = createTestExecutor(DecimalExtensionFactory)
        execute.run(
            unitOfWork(
                """
                local decimal = require('decimal')
                local x = decimal.new('512')
                local y = 1024
                local result = x - y
                assert(type(result) == 'decimal')
                assert(decimal.to_string(result) == '-512')
            """
            )
        )
    }


    @Test
    fun `decimal, decimal`() {
        val execute = createTestExecutor(DecimalExtensionFactory)
        execute.run(
            unitOfWork(
                """
                local decimal = require('decimal')
                local x = decimal.new('512')
                local y = decimal.new(1024)
                local result = x - y
                assert(type(result) == 'decimal')
                assert(decimal.to_string(result) == '-512')
            """
            )
        )
    }

    @Test
    fun `number, decimal`() {
        val execute = createTestExecutor(DecimalExtensionFactory)
        execute.run(
            unitOfWork(
                """
                local decimal = require('decimal')
                local x = 512
                local y = decimal.new(1024)
                local result = x - y
                assert(type(result) == 'decimal')
                assert(decimal.to_string(result) == '-512')
            """
            )
        )
    }
}
