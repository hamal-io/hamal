package io.hamal.extension.std.decimal

import io.hamal.runner.test.AbstractRunnerTest
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class DivTest : AbstractRunnerTest() {
    @Test
    fun `decimal, number`() {
        val runner = createTestRunner(
            extensionFactories = listOf(ExtensionStdDecimalFactory)
        )

        runner.run(
            unitOfWork(
                """
                local decimal = require('std.decimal').create()
                local x = decimal.new('3.14')
                local y = 2
                local result = x / y
                assert(type(result) == 'decimal')
                assert(decimal.to_string(result) == '1.57')
            """
            )
        )
    }


    @Test
    fun `decimal, decimal`() {
        val runner = createTestRunner(
            extensionFactories = listOf(ExtensionStdDecimalFactory)
        )

        runner.run(
            unitOfWork(
                """
                local decimal = require('std.decimal').create()
                local x = decimal.new('3.14')
                local y = decimal.new(2)
                local result = x / y
                assert(type(result) == 'decimal')
                assert(decimal.to_string(result) == '1.57')
            """
            )
        )
    }

    @Disabled //204-breaks all tests
    @Test
    fun `number, decimal`() {
        val runner = createTestRunner(
            extensionFactories = listOf(ExtensionStdDecimalFactory)
        )

        runner.run(
            unitOfWork(
                """
                local decimal = require('std.decimal').create()
                local x = 3.14
                local y = decimal.new(2)
                local result = x / y
                assert(type(result) == 'decimal')
                assert(decimal.to_string(result) == '1.57')
            """
            )
        )
    }
}
