package io.hamal.extension.std.decimal

import io.hamal.runner.test.AbstractRunnerTest
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class DecimalTest : AbstractRunnerTest() {
    @Test
    fun `Can import decimal`() {
        val runner = createTestRunner(
            extensionFactories = listOf(ExtensionStdDecimalFactory)
        )

        runner.run(
            unitOfWork(
                """
            local decimal = require('std.decimal').create()
            assert(decimal)
            """
            )
        )
    }

    @Test
    fun `Creating decimal and iterating over _G does not crash`() {
        val runner = createTestRunner(
            extensionFactories = listOf(ExtensionStdDecimalFactory)
        )

        runner.run(
            unitOfWork(
                """
            local decimal = require('std.decimal').create()
            local b = decimal.new('3.14')
            for k,v in pairs(_G) do print(k,v) end
            """
            )
        )
    }

    @Disabled //204-breaks all tests
    @Test
    fun `Can create new decimal instance by number`() {
        val runner = createTestRunner(
            extensionFactories = listOf(ExtensionStdDecimalFactory)
        )

        runner.run(
            unitOfWork(
                """
            local decimal = require('std.decimal').create()
            local a = decimal.new(42.24)
            assert(decimal.to_string(a) == '42.24')
            """
            )
        )
    }

    @Test
    fun `Can create new decimal instance by string`() {
        val runner = createTestRunner(
            extensionFactories = listOf(ExtensionStdDecimalFactory)
        )

        runner.run(
            unitOfWork(
                """
            local decimal = require('std.decimal').create()
            local b = decimal.new('3.14')
            assert(decimal.to_string(b) == '3.14')
        """
            )
        )
    }
}
