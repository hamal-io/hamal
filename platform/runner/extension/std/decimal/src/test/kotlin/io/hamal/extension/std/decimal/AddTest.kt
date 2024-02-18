package io.hamal.extension.std.decimal

import io.hamal.runner.test.AbstractRunnerTest
import org.junit.jupiter.api.Test

internal class AddTest : AbstractRunnerTest() {
    @Test
    fun `decimal, number`() {
        val runner = createTestRunner(
            extensionFactories = listOf(ExtensionDecimalFactory)
        )
        runner.run(
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
        val runner = createTestRunner(
            extensionFactories = listOf(ExtensionDecimalFactory)
        )
        runner.run(
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
        val runner = createTestRunner(
            extensionFactories = listOf(ExtensionDecimalFactory)
        )
        runner.run(
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
