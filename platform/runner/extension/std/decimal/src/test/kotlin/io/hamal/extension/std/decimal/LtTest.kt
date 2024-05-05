package io.hamal.extension.std.decimal

import io.hamal.runner.test.AbstractRunnerTest
import org.junit.jupiter.api.Test

internal class LtTest : AbstractRunnerTest() {
    @Test
    fun `decimal, number`() {
        val runner = createTestRunner(
            extensionFactories = listOf(ExtensionStdDecimalFactory)
        )
        runner.run(
            unitOfWork(
                """
                local decimal = require('std.decimal').create()
                local a = decimal.new('512')
                local b = 1024
                local c = 512
                local d = 256
                
                assert( (a < b) == true )
                assert( (a < c) == false )
                assert( (a < d) == false )
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
                local a = decimal.new('512')
                local b = decimal.new(1024)
                local c = decimal.new(512)
                local d = decimal.new(256)
                
                assert( (a < b) == true )
                assert( (a < c) == false )
                assert( (a < d) == false )
            """
            )
        )
    }

    @Test
    fun `number, decimal`() {
        val runner = createTestRunner(
            extensionFactories = listOf(ExtensionStdDecimalFactory)
        )
        runner.run(
            unitOfWork(
                """
                local decimal = require('std.decimal').create()
                local a = 512
                local b = decimal.new(1024)
                local c = decimal.new(512)
                local d = decimal.new(256)
                
                assert( (a < b) == true )
                assert( (a < c) == false )
                assert( (a < d) == false )
            """
            )
        )
    }
}
