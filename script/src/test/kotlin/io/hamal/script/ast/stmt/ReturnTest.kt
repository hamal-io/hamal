package io.hamal.script.ast.stmt

import io.hamal.script.ast.expr.NilLiteral
import io.hamal.script.ast.expr.NumberLiteral
import io.hamal.script.token.Token.Type
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class ReturnTest : AbstractStatementTest() {
    @Nested
    @DisplayName("Parse()")
    inner class ParseTest {
        @Test
        fun `Return statement without expression`() {
            runTest(Return.Parse, "return end") { result, tokens ->
                assertThat(result, equalTo(Return(NilLiteral())))
                // its intentional that there is end left
                tokens.inOrder(Type.End, Type.Eof)
            }
        }

        @Test
        fun `Return statement with number literal`() {
            runTest(Return.Parse, "return 1 end") { result, tokens ->
                assertThat(result, equalTo(Return(NumberLiteral(1))))
                // its intentional that there is end left
                tokens.inOrder(Type.End, Type.Eof)
            }
        }
    }
}