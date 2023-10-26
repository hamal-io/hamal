package io.hamal.script.std.decimal

import AbstractExtensionTest
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class MulTest : AbstractExtensionTest() {
    @Test
    fun `decimal, number`() {
        val runner = createTestRunner(
            scriptFactories = listOf(DecimalScriptFactory)
        )
        runner.run(
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
        val runner = createTestRunner(
            scriptFactories = listOf(DecimalScriptFactory)
        )
        runner.run(
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

    @Disabled("Floating Point Problem")
    @Test
    fun `number, decimal`() {
        val runner = createTestRunner(
            scriptFactories = listOf(DecimalScriptFactory)
        )
        runner.run(
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
