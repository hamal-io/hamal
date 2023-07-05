package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.impl.anotherPosition
import io.hamal.lib.script.impl.somePosition
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class IdentifierTest : AbstractExpressionTest() {
    @Nested
    inner class EqualsTest {
        @Test
        fun `Equal if underlying values are equal`() {
            assertEquals(
                IdentifierLiteral(somePosition, "SomeIdentifier"),
                IdentifierLiteral(somePosition, "SomeIdentifier")
            )

            assertEquals(
                IdentifierLiteral(somePosition, "SomeIdentifier"),
                IdentifierLiteral(anotherPosition, "SomeIdentifier")
            )

        }

        @Test
        fun `Not Equal if underlying values are different`() {
            assertNotEquals(
                IdentifierLiteral(somePosition, "SomeIdentifier"),
                IdentifierLiteral(somePosition, "AnotherIdentifier")
            )
        }
    }

    @Nested
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if values are equal`() {
            assertEquals(
                IdentifierLiteral(somePosition, "SomeIdentifier").hashCode(),
                IdentifierLiteral(somePosition, "SomeIdentifier").hashCode()
            )

            assertEquals(
                IdentifierLiteral(anotherPosition, "SomeIdentifier").hashCode(),
                IdentifierLiteral(anotherPosition, "SomeIdentifier").hashCode()
            )
        }

        @Test
        fun `Different hashcode if values are different`() {
            assertNotEquals(
                IdentifierLiteral(somePosition, "SomeIdentifier").hashCode(),
                IdentifierLiteral(anotherPosition, "AnotherIdentifier").hashCode()
            )
        }
    }

    @Nested
    inner class ParseTest {
        @Test
        fun ident() {
            runLiteralTest(IdentifierLiteral.Parse, "some_variable") { result, tokens ->
                assertThat(result, equalTo(IdentifierLiteral(somePosition, "some_variable")))
                tokens.consumed()
            }
        }
    }
}
