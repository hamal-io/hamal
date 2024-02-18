package io.hamal.extension.std.decimal

import io.hamal.runner.test.AbstractRunnerTest
import org.junit.jupiter.api.Test

internal class SqrtTest : AbstractRunnerTest() {
    @Test
    fun `decimal, decimal`() {
        val runner = createTestRunner(
            extensionFactories = listOf(ExtensionDecimalFactory)
        )
        runner.run(
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
