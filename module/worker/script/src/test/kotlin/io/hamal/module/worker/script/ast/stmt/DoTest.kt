package io.hamal.module.worker.script.ast.stmt

import io.hamal.module.worker.script.ast.expr.Nil
import io.hamal.module.worker.script.ast.stmt.Do.ParseDo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class DoTest : AbstractStatementTest() {
    @Test
    fun `do end`() {
        runTest(ParseDo, "do end") { result, tokens ->
            assertThat(result, equalTo(Do(Block.empty)))
            assertAllTokensConsumed(tokens)
        }
    }

    @Test
    fun `do return end`() {
        runTest(ParseDo, "do return end") { result, tokens ->
            assertThat(result, equalTo(Do(Block(Return(Nil())))))
            assertAllTokensConsumed(tokens)
        }
    }
}