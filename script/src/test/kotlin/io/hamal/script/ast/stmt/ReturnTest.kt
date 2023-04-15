package io.hamal.script.ast.stmt

import io.hamal.script.ast.expr.Nil
import io.hamal.script.ast.expr.Number
import io.hamal.script.token.Token.Type
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class ReturnTest : AbstractStatementTest() {

    @Test
    fun `Return statement without expression`() {
        runTest(Return.ParseReturn, "return end") { result, tokens ->
            assertThat(result, equalTo(Return(Nil())))
            // its intentional that there is end left
            tokens.inOrder(Type.End, Type.Eof)
        }
    }

    @Test
    fun `Return statement with number literal`() {
        runTest(Return.ParseReturn, "return 1 end") { result, tokens ->
            assertThat(result, equalTo(Return(Number(1))))
            // its intentional that there is end left
            tokens.inOrder(Type.End, Type.Eof)
        }
    }
}