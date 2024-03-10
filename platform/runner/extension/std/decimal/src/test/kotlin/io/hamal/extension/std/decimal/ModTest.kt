package io.hamal.extension.std.decimal

import io.hamal.runner.test.AbstractRunnerTest
import org.junit.jupiter.api.Test

internal class ModTest : AbstractRunnerTest() {
    @Test
    fun `decimal, number`() {
        val runner = createTestRunner(
            extensionFactories = listOf(ExtensionDecimalFactory)
        )
        runner.run(
            unitOfWork(
                """
                local decimal = require('decimal')
                local x = decimal.new('3.14')
                local y = 2
                local result = x % y
                assert(type(result) == 'decimal')
                assert(decimal.to_string(result) == '1.14')
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
                local x = decimal.new('3.14')
                local y = decimal.new(2)
                local result = x % y
                assert(type(result) == 'decimal')
                assert(decimal.to_string(result) == '1.14')
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
                local x = 3.14
                local y = decimal.new(2)
                local result = x % y
                assert(type(result) == 'decimal')
                assert(decimal.to_string(result) == '1.14')
            """
            )
        )
    }
}
