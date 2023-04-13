package io.hamal.module.worker.script.ast.expr

import io.hamal.module.worker.script.ast.AbstractAstTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class LiteralTest : AbstractAstTest() {

    @Nested
    @DisplayName("NumberLiteral")
    inner class NumberLiteralTest {
        @Test
        fun `Parse number token`() {
            val result = parseSimpleLiteralExpression(NumberLiteral.ParseNumberLiteral, "28.10")
            result.verifyPrecedence("28.10")
        }
    }

    @Nested
    @DisplayName("StringLiteral")
    inner class StringLiteralTest {
        @Test
        fun `Parses string token`() {
            val result = parseSimpleLiteralExpression(StringLiteral.ParseStringLiteral, "'hello hamal'")
            result.verifyPrecedence("'hello hamal'")
        }
    }

    @Nested
    @DisplayName("TrueLiteral")
    inner class TrueLiteralTest {
        @Test
        fun `Parse true`() {
            val result = parseSimpleLiteralExpression(TrueLiteral.ParseTrueLiteral, "true")
            result.verifyPrecedence("true")
        }
    }

    @Nested
    @DisplayName("FalseLiteral")
    inner class FalseLiteralTest {
        @Test
        fun `Parse false`() {
            val result = parseSimpleLiteralExpression(FalseLiteral.ParseFalseLiteral, "false")
            result.verifyPrecedence("false")
        }
    }

    @Nested
    @DisplayName("NilLiteral")
    inner class NilLiteralTest {
        @Test
        fun `Parse nil`() {
            val result = parseSimpleLiteralExpression(NilLiteral.ParseNilLiteral, "nil")
            result.verifyPrecedence("nil")
        }
    }

    @Nested
    @DisplayName("FunctionLiteral")
    inner class FunctionLiteralTest {
        @Test
        fun `Parse empty function`() {
            val result = parseExpression(
                FunctionLiteral.ParseFunctionLiteral,
                """
                function empty() 
                end
                """.trimIndent()
            ) as FunctionLiteral
            assertThat(result.identifier, equalTo(Identifier("empty")))
            assertThat(result.parameters, hasSize(0))
            assertThat(result.block, hasSize(0))
        }

        @Test
        fun `Parse empty function with single argument`() {
            val result = parseExpression(
                FunctionLiteral.ParseFunctionLiteral,
                """
                function empty_with_single_param(param_one) end
                """.trimIndent()
            ) as FunctionLiteral
            assertThat(result.identifier, equalTo(Identifier("empty_with_single_param")))
            assertThat(result.parameters, equalTo(listOf(Identifier("param_one"))))
            assertThat(result.block, hasSize(0))
        }

        @Test
        fun `Parse empty function with multiple arguments`() {
            val result = parseExpression(
                FunctionLiteral.ParseFunctionLiteral,
                """
                function empty_with_params(one,two,three) end
                """.trimIndent()
            ) as FunctionLiteral
            assertThat(result.identifier, equalTo(Identifier("empty_with_params")))
            assertThat(
                result.parameters, equalTo(
                    listOf(
                        Identifier("one"),
                        Identifier("two"),
                        Identifier("three")
                    )
                )
            )
            assertThat(result.block, hasSize(0))
        }
    }
}