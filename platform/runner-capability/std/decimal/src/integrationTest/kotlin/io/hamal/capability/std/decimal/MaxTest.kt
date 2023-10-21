package io.hamal.capability.std.decimal

import AbstractCapabilityTest
import io.hamal.capability.std.log.DecimalCapabilityFactory
import org.junit.jupiter.api.Test

internal class MaxTest : AbstractCapabilityTest() {
    @Test
    fun `decimal, number`() {
        val execute = createTestExecutor(DecimalCapabilityFactory)
        execute.run(
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
        val execute = createTestExecutor(DecimalCapabilityFactory)
        execute.run(
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
        val execute = createTestExecutor(DecimalCapabilityFactory)
        execute.run(
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
