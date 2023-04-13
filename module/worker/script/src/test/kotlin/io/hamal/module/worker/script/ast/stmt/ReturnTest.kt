package io.hamal.module.worker.script.ast.stmt

import org.junit.jupiter.api.Test

internal class ReturnTest : AbstractStatementTest() {

    @Test
    fun `Return statement without expression`() {
        runTest(Return.ParseReturn, "return end") { result, tokens ->
//            MatcherAssert.assertThat(result.blockStatement, Matchers.hasSize(0))
            // its intentional that there is end left
            assertAllTokensConsumed(tokens)
        }
    }

    @Test
    fun `Return statement with number literal`() {
        runTest(Return.ParseReturn, "return 1 end") { result, tokens ->
//            MatcherAssert.assertThat(result.blockStatement, Matchers.hasSize(0))
            // its intentional that there is end left
            assertAllTokensConsumed(tokens)
        }
    }

    private fun testInstance(code: String) = Return.ParseReturn(parserContextOf(code))
}