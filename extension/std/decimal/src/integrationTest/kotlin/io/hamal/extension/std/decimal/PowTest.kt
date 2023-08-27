package io.hamal.extension.std.decimal

import AbstractExtensionTest
import io.hamal.extension.std.log.DecimalExtensionFactory
import org.junit.jupiter.api.Test

internal class PowTest : AbstractExtensionTest() {
    @Test
    fun `decimal, decimal`() {
        val execute = createTestExecutor(DecimalExtensionFactory)
        execute(
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
