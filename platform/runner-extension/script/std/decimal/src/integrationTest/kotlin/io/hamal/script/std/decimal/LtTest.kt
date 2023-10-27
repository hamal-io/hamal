package io.hamal.script.std.decimal

import AbstractExtensionTest
import org.junit.jupiter.api.Test

internal class LtTest : AbstractExtensionTest() {
    @Test
    fun `decimal, number`() {
        val runner = createTestRunner(
            scriptFactories = listOf(DecimalScriptFactory)
        )
        runner.run(
            unitOfWork(
                """
                local decimal = require('decimal')
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
            scriptFactories = listOf(DecimalScriptFactory)
        )
        runner.run(
            unitOfWork(
                """
                local decimal = require('decimal')
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
            scriptFactories = listOf(DecimalScriptFactory)
        )
        runner.run(
            unitOfWork(
                """
                local decimal = require('decimal')
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