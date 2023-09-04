package io.hamal.extension.std.decimal

import AbstractExtensionTest
import io.hamal.extension.std.log.DecimalExtensionFactory
import org.junit.jupiter.api.Test

internal class SqrtTest : AbstractExtensionTest() {
    @Test
    fun `decimal, decimal`() {
        val execute = createTestExecutor(DecimalExtensionFactory)
        execute.run(
            unitOfWork(
                """
                local decimal = require('decimal')
                local x = decimal.new('512')
                local result = decimal.sqrt(x)
                
                assert(type(result) == 'decimal')
                assert(decimal.to_string(result) == '22.627416997969520780827019587355169257')
            """
            )
        )
    }
}
