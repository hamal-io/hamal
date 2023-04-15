package io.hamal.script.ast.stmt

import io.hamal.script.ast.expr.Nil
import io.hamal.script.ast.stmt.Do.Parse
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class DoTest : AbstractStatementTest() {
    @Nested
    @DisplayName("Parse()")
    inner class ParseTest {
        @Test
        fun `do end`() {
            runTest(Parse, "do end") { result, tokens ->
                assertThat(result, equalTo(Do(Block.empty)))
                tokens.wereConsumed()
            }
        }

        @Test
        fun `do return end`() {
            runTest(Parse, "do return end") { result, tokens ->
                assertThat(result, equalTo(Do(Block(Return(Nil())))))
                tokens.wereConsumed()
            }
        }
    }
}