package io.hamal.extension.std.decimal

import AbstractRunnerTest
import org.junit.jupiter.api.Test

internal class GtTest : AbstractRunnerTest() {
    @Test
    fun `decimal, number`() {
        val runner = createTestRunner(
            extensionFactories = listOf(ExtensionDecimalFactory)
        )

        runner.run(
            unitOfWork(
                """
                local decimal = require('decimal')
                local a = decimal.new('512')
                local b = 1024
                local c = 512
                local d = 256
                
                assert( (a > b) == false )
                assert( (a > c) == false )
                assert( (a > d) == true )
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
                local a = decimal.new('512')
                local b = decimal.new(1024)
                local c = decimal.new(512)
                local d = decimal.new(256)
                
                assert( (a > b) == false )
                assert( (a > c) == false )
                assert( (a > d) == true )
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
                local a = 512
                local b = decimal.new(1024)
                local c = decimal.new(512)
                local d = decimal.new(256)
                
                assert( (a > b) == false )
                assert( (a > c) == false )
                assert( (a > d) == true )
            """
            )
        )
    }
}