package io.hamal.extension.std.decimal

import AbstractCapabilityTest
import io.hamal.extension.std.log.DecimalCapabilityFactory
import org.junit.jupiter.api.Test

internal class PowTest : AbstractCapabilityTest() {
    @Test
    fun `decimal, decimal`() {
        val execute = createTestExecutor(DecimalCapabilityFactory)
        execute.run(
            unitOfWork(
                """
                local decimal = require('decimal')
                local x = decimal.new(2)
                local result = x ^ 10
                
                assert(type(result) == 'decimal')
                assert(decimal.to_string(result) == '1024')
            """
            )
        )
    }
}
