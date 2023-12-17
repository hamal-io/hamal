package io.hamal.extension.std.decimal

import AbstractRunnerTest
import org.junit.jupiter.api.Test

internal class MinTest : AbstractRunnerTest() {
    @Test
    fun `decimal, number`() {
        val runner = createTestRunner(
            extensionFactories = listOf(DecimalExtensionFactory)
        )
        runner.run(
            unitOfWork(
                """
                local decimal = require('decimal')
                local x = decimal.new('512')
                local y = 1024

                assert(decimal.min(x,x) == x)
                assert(decimal.min(x,y) == x)
            """
            )
        )
    }


    @Test
    fun `decimal, decimal`() {
        val runner = createTestRunner(
            extensionFactories = listOf(DecimalExtensionFactory)
        )
        runner.run(
            unitOfWork(
                """
                local decimal = require('decimal')
                local x = decimal.new('512')
                local y = decimal.new(1024)

                assert(decimal.min(x,x) == x)
                assert(decimal.min(x,y) == x)
            """
            )
        )
    }

    @Test
    fun `number, decimal`() {
        val runner = createTestRunner(
            extensionFactories = listOf(DecimalExtensionFactory)
        )
        runner.run(
            unitOfWork(
                """
              local decimal = require('decimal')
                local x = 512
                local y = decimal.new(1024)

                assert(decimal.min(x,x) == decimal.new(512))
                assert(decimal.min(x,y) == decimal.new(512))
            """
            )
        )
    }
}
