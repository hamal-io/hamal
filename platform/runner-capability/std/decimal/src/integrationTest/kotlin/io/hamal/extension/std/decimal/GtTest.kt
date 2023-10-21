package io.hamal.extension.std.decimal

import AbstractCapabilityTest
import io.hamal.extension.std.log.DecimalCapabilityFactory
import org.junit.jupiter.api.Test

internal class GtTest : AbstractCapabilityTest() {
    @Test
    fun `decimal, number`() {
        val execute = createTestExecutor(DecimalCapabilityFactory)
        execute.run(
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
        val execute = createTestExecutor(DecimalCapabilityFactory)
        execute.run(
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
        val execute = createTestExecutor(DecimalCapabilityFactory)
        execute.run(
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
