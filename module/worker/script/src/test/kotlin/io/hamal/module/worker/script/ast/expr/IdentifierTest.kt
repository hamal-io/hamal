package io.hamal.module.worker.script.ast.expr

import io.hamal.module.worker.script.token.Token.Type
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class IdentifierTest : AbstractExpressionTest() {
    @Nested
    @DisplayName("Parse()")
    inner class ParseTest {
        @Test
        fun `Parses identifier`() {
            runLiteralTest(Identifier.Parse, "some_variable") { result, tokens ->
                assertThat(result, equalTo(Identifier("some_variable")))
                tokens.inOrder(Type.Identifier, Type.Eof)
            }
        }
    }
}