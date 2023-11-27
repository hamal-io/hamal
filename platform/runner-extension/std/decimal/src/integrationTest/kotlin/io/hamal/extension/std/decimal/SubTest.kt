package io.hamal.extension.std.decimal

import AbstractRunnerTest
import org.junit.jupiter.api.Test

internal class SubTest : AbstractRunnerTest() {
    @Test
    fun `decimal, number`() {
        val runner = createTestRunner(
            extensionFactories = listOf(DecimalExtensionFactory)
        )
        runner.run(
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
        val runner = createTestRunner(
            extensionFactories = listOf(DecimalExtensionFactory)
        )
        runner.run(
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
        val runner = createTestRunner(
            extensionFactories = listOf(DecimalExtensionFactory)
        )
        runner.run(
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
