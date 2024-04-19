package io.hamal.extension.std.decimal

import io.hamal.runner.test.AbstractRunnerTest
import org.junit.jupiter.api.Test

internal class NegTest : AbstractRunnerTest() {
    @Test
    fun `decimal, decimal`() {
        val runner = createTestRunner(
            extensionFactories = listOf(ExtensionDecimalFactory)
        )
        runner.run(
            unitOfWork(
                """
                local decimal = require('std.decimal')
                local x = decimal.new(512)
                local y = decimal.new(-512)
                
                local result_one = decimal.neg(x)
                local result_two = decimal.neg(y)
                local result_three = -x
                local result_four = -y
                
                assert(type(result_one) == 'decimal')
                assert(type(result_two) == 'decimal')
                assert(type(result_three) == 'decimal')
                assert(type(result_four) == 'decimal')

                assert(decimal.to_string(result_one) == '-512')
                assert(decimal.to_string(result_two) == '512')
                assert(decimal.to_string(result_three) == '-512')
                assert(decimal.to_string(result_four) == '512')
            """
            )
        )
    }
}
