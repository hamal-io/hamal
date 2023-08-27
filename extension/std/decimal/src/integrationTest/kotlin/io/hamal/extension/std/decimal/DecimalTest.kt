package io.hamal.extension.std.decimal

import AbstractExtensionTest
import io.hamal.extension.std.log.DecimalExtensionFactory
import org.junit.jupiter.api.Test

internal class DecimalTest : AbstractExtensionTest() {
    @Test
    fun `Can import decimal`() {
        val execute = createTestExecutor(DecimalExtensionFactory)
        execute(
            unitOfWork(
                """
            local decimal = require('decimal')
            assert(decimal)
            """
            )
        )
    }

    @Test
    fun `Creating decimal and iterating over _G does not crash`() {
        val execute = createTestExecutor(DecimalExtensionFactory)
        execute(
            unitOfWork(
                """
            local decimal = require('decimal')
            local b = decimal.new('3.14')
            for k,v in pairs(_G) do print(k,v) end
            """
            )
        )
    }

    @Test
    fun `Can create new decimal instance by number`() {
        val execute = createTestExecutor(DecimalExtensionFactory)
        execute(
            unitOfWork(
                """
            local decimal = require('decimal')
            local a = decimal.new(42.24)
            assert(decimal.to_string(a) == '42.24')
            """
            )
        )
    }

    @Test
    fun `Can create new decimal instance by string`() {
        val execute = createTestExecutor(DecimalExtensionFactory)
        execute(
            unitOfWork(
                """
            local decimal = require('decimal')
            local b = decimal.new('3.14')
            assert(decimal.to_string(b) == '3.14')
        """
            )
        )
    }
}
