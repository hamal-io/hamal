package io.hamal.extension.std.decimal

import io.hamal.runner.test.AbstractRunnerTest
import org.junit.jupiter.api.Test

internal class AbsTest : AbstractRunnerTest() {
    @Test
    fun `decimal, decimal`() {
        val runner = createTestRunner(
            extensionFactories = listOf(ExtensionStdDecimalFactory)
        )
        runner.run(
            unitOfWork(
                """
                local decimal = require('std.decimal').create()
                local x = decimal.new('512')
                local y = decimal.new('-512')
                local same = decimal.abs(x)
                local updated = decimal.abs(y)
                
                assert(type(same) == 'decimal')
                assert(type(updated) == 'decimal')
                
                assert(decimal.to_string(same) == '512')
                assert(decimal.to_string(updated) == '512')
            """
            )
        )
    }
}
