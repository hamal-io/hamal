package io.hamal.extension.std.decimal

import AbstractExtensionTest
import io.hamal.extension.std.log.DecimalExtensionFactory
import org.junit.jupiter.api.Test

internal class MaxTest : AbstractExtensionTest() {
    @Test
    fun `decimal, number`() {
        val execute = createTestExecutor(DecimalExtensionFactory)
        execute(
            unitOfWork(
                """
                local decimal = require('decimal')
                local x = decimal.new('512')
                local y = 1024

                assert(decimal.max(x,x) == x)
                assert(decimal.max(x,y) == decimal.new(1024))
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

                assert(decimal.max(x,x) == x)
                assert(decimal.max(x,y) == y)
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

                assert(decimal.max(x,x) == decimal.new(512))
                assert(decimal.max(x,y) == y)
            """
            )
        )
    }
}
