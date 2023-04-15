package io.hamal.script.ast.expr

import io.hamal.script.token.Token.Type
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class IdentifierTest : AbstractExpressionTest() {

    @Nested
    @DisplayName("equals()")
    inner class EqualsTest {
        @Test
        fun `Equals if underlying values are equal`() {
            Assertions.assertEquals(
                Identifier("SomeIdentifier"),
                Identifier("SomeIdentifier")
            )
        }

        @Test
        fun `Not equals if underlying values are different`() {
            Assertions.assertNotEquals(
                Identifier("SomeIdentifier"),
                Identifier("AnotherIdentifier")
            )
        }
    }

    @Nested
    @DisplayName("hashCode()")
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if values are equal`() {
            Assertions.assertEquals(
                Identifier("SomeIdentifier").hashCode(),
                Identifier("SomeIdentifier").hashCode()
            )
        }

        @Test
        fun `Different hashcode if values are different`() {
            Assertions.assertNotEquals(
                Identifier("SomeIdentifier").hashCode(),
                Identifier("AnotherIdentifier").hashCode()
            )
        }
    }

    @Nested
    @DisplayName("Parse()")
    inner class ParseTest {
        @Test
        fun identifier() {
            runLiteralTest(Identifier.Parse, "some_variable") { result, tokens ->
                assertThat(result, equalTo(Identifier("some_variable")))
                tokens.inOrder(Type.Identifier, Type.Eof)
            }
        }
    }
}
