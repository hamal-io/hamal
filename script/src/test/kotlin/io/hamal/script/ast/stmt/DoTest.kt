package io.hamal.script.ast.stmt

import io.hamal.script.ast.expr.Nil
import io.hamal.script.ast.stmt.Do.ParseDo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class DoTest : AbstractStatementTest() {
    @Test
    fun `do end`() {
        runTest(ParseDo, "do end") { result, tokens ->
            assertThat(result, equalTo(Do(Block.empty)))
            tokens.wereConsumed()
        }
    }

    @Test
    fun `do return end`() {
        runTest(ParseDo, "do return end") { result, tokens ->
            assertThat(result, equalTo(Do(Block(Return(Nil())))))
            tokens.wereConsumed()
        }
    }
}