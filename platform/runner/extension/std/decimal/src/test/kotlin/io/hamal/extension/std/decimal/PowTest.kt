package io.hamal.extension.std.decimal

import io.hamal.runner.test.AbstractRunnerTest
import org.junit.jupiter.api.Test

internal class PowTest : AbstractRunnerTest() {
    @Test
    fun `decimal, decimal`() {
        val runner = createTestRunner(
            extensionFactories = listOf(ExtensionStdDecimalFactory)
        )
        runner.run(
            unitOfWork(
                """
                local decimal = require('std.decimal').create()
                local x = decimal.new(2)
                local result = x ^ 10
                
                assert(type(result) == 'decimal')
                assert(decimal.to_string(result) == '1024')
            """
            )
        )
    }
}
