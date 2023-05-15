package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.impl.token.Token.Type
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class IdentifierLiteralTest : AbstractExpressionTest() {

    @Nested
    @DisplayName("equals()")
    inner class EqualsTest {
        @Test
        fun `Equal if underlying values are equal`() {
            Assertions.assertEquals(
                IdentifierLiteral("SomeIdentifier"),
                IdentifierLiteral("SomeIdentifier")
            )
        }

        @Test
        fun `Not Equal if underlying values are different`() {
            Assertions.assertNotEquals(
                IdentifierLiteral("SomeIdentifier"),
                IdentifierLiteral("AnotherIdentifier")
            )
        }
    }

    @Nested
    @DisplayName("hashCode()")
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if values are equal`() {
            Assertions.assertEquals(
                IdentifierLiteral("SomeIdentifier").hashCode(),
                IdentifierLiteral("SomeIdentifier").hashCode()
            )
        }

        @Test
        fun `Different hashcode if values are different`() {
            Assertions.assertNotEquals(
                IdentifierLiteral("SomeIdentifier").hashCode(),
                IdentifierLiteral("AnotherIdentifier").hashCode()
            )
        }
    }

    @Nested
    @DisplayName("Parse()")
    inner class ParseTest {
        @Test
        fun identifier() {
            runLiteralTest(IdentifierLiteral.Parse, "some_variable") { result, tokens ->
                assertThat(result, equalTo(IdentifierLiteral("some_variable")))
                tokens.inOrder(Type.Identifier, Type.Eof)
            }
        }
    }
}
