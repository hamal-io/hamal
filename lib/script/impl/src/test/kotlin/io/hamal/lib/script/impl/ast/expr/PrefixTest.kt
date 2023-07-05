package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.impl.somePosition
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class PrefixTest : AbstractExpressionTest() {
    @Nested
    inner class ParseTest {
        @Test
        fun `prefix expression`() {
            runTest(PrefixExpression.Parse, "-123") { result, tokens ->
                assertThat(
                    result,
                    equalTo(PrefixExpression(somePosition, Operator.Minus, NumberLiteral(somePosition, 123)))
                )
                tokens.consumed()
            }
        }
    }

}