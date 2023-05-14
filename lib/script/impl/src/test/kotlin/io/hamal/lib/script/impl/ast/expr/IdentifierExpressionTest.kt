package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.impl.token.Token.Type
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class IdentifierExpressionTest : AbstractExpressionTest() {

    @Nested
    @DisplayName("equals()")
    inner class EqualsTest {
        @Test
        fun `Equal if underlying values are equal`() {
            Assertions.assertEquals(
                IdentifierExpression("SomeIdentifier"),
                IdentifierExpression("SomeIdentifier")
            )
        }

        @Test
        fun `Not Equal if underlying values are different`() {
            Assertions.assertNotEquals(
                IdentifierExpression("SomeIdentifier"),
                IdentifierExpression("AnotherIdentifier")
            )
        }
    }

    @Nested
    @DisplayName("hashCode()")
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if values are equal`() {
            Assertions.assertEquals(
                IdentifierExpression("SomeIdentifier").hashCode(),
                IdentifierExpression("SomeIdentifier").hashCode()
            )
        }

        @Test
        fun `Different hashcode if values are different`() {
            Assertions.assertNotEquals(
                IdentifierExpression("SomeIdentifier").hashCode(),
                IdentifierExpression("AnotherIdentifier").hashCode()
            )
        }
    }

    @Nested
    @DisplayName("Parse()")
    inner class ParseTest {
        @Test
        fun identifier() {
            runLiteralTest(IdentifierExpression.Parse, "some_variable") { result, tokens ->
                assertThat(result, equalTo(IdentifierExpression("some_variable")))
                tokens.inOrder(Type.Identifier, Type.Eof)
            }
        }
    }
}
