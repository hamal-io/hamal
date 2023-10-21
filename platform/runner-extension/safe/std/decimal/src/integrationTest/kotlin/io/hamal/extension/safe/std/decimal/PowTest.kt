package io.hamal.extension.safe.std.decimal

import AbstractExtensionTest
import org.junit.jupiter.api.Test

internal class PowTest : AbstractExtensionTest() {
    @Test
    fun `decimal, decimal`() {
        val runner = createTestRunner(
            safeFactories = listOf(DecimalSafeFactory)
        )
        runner.run(
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
