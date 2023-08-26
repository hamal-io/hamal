package io.hamal.extension.std.log

import AbstractExtensionTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
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
            assert(a.to_string() == '42.24')
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
            assert(b.to_string() == '3.14')
        """
            )
        )
    }

    @Nested
    @DisplayName("Operations:")
    inner class OperationTest {

        @Test
        fun div() {
            val execute = createTestExecutor(DecimalExtensionFactory)
            execute(
                unitOfWork(
                    """
            local decimal = require('decimal')
            local b = decimal.new('3.14')
            print(b/2)
        """
                )
            )
        }

    }

}
