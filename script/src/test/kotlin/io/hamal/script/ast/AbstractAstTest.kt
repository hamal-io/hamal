package io.hamal.script.ast

import io.hamal.script.token.Token
import io.hamal.script.token.Token.Type.Eof
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.greaterThanOrEqualTo

internal abstract class AbstractAstTest {

    fun ArrayDeque<Token>.inOrder(vararg types: Token.Type) {
        assertThat("Expected number of tokens ${types.size} got $size", size, greaterThanOrEqualTo(types.size))

        take(types.size)
            .forEachIndexed { index, token ->
                assertThat(
                    "#{$index} - Expected to be of type ${types[index]} but got ${token.type}",
                    token.type,
                    equalTo(types[index])
                )
            }

    }

    fun ArrayDeque<Token>.wereConsumed() {
        assertThat("All tokens were consumed except EOF", size, equalTo(1))
        assertThat("Last token must be EOF", this[0].type, equalTo(Eof))
    }
}