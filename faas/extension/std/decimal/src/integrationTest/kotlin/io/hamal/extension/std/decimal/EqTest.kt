package io.hamal.extension.std.decimal

import AbstractExtensionTest
import io.hamal.extension.std.log.DecimalExtensionFactory
import org.junit.jupiter.api.Test

internal class EqTest : AbstractExtensionTest() {
    @Test
    fun `decimal, number`() {
        val execute = createTestExecutor(DecimalExtensionFactory)
        execute(
            unitOfWork(
                """
                local decimal = require('decimal')
                local x = decimal.new('512')
                local y = 1024
                local z = 512
                
                assert(x ~= y)
                assert(x ~= z)
            """
            )
        )
    }


    @Test
    fun `decimal, decimal`() {
        val execute = createTestExecutor(DecimalExtensionFactory)
        execute(
            unitOfWork(
                """
                local decimal = require('decimal')
                local x = decimal.new('512')
                local y = decimal.new(1024)
                local z = decimal.new(512)
                
                assert(x ~= y)
                assert(x == z)
            """
            )
        )
    }

    @Test
    fun `number, decimal`() {
        val execute = createTestExecutor(DecimalExtensionFactory)
        execute(
            unitOfWork(
                """
                local decimal = require('decimal')
                local x = 512
                local y = decimal.new(1024)
                local z = decimal.new(512)
                
                assert(x ~= y)
                assert(x ~= z)
            """
            )
        )
    }
}
