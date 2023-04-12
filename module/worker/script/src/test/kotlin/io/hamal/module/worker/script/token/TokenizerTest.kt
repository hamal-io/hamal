package io.hamal.module.worker.script.token

import io.hamal.lib.meta.exception.IllegalStateException
import io.hamal.module.worker.script.token.*
import io.hamal.module.worker.script.token.Token.*
import io.hamal.module.worker.script.token.Token.Type.Error
import io.hamal.module.worker.script.token.Token.Type.HexNumberLiteral
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource


class TokenizerTest {

    @Nested
    @DisplayName("DefaultImpl")
    inner class DefaultImplTest {

        @Nested
        @DisplayName("isAtEnd()")
        inner class IsAtEndTest {
            @Test
            fun `Advances until reaches end`() {
                val testInstance = Tokenizer.DefaultImpl("num")
                assertFalse(testInstance.isAtEnd())
                testInstance.advance()
                assertFalse(testInstance.isAtEnd())
                testInstance.advance()
                assertFalse(testInstance.isAtEnd())
                testInstance.advance()
                assertTrue(testInstance.isAtEnd())
            }
        }

        @Nested
        @DisplayName("peek()")
        inner class PeekTest {
            @Test
            fun `Returns the character of the current position`() {
                val testInstance = Tokenizer.DefaultImpl("hamal")
                assertThat(testInstance.peek(), equalTo('h'))
                testInstance.advance()
                assertThat(testInstance.peek(), equalTo('a'))
            }

            @Test
            fun `Throws exception if there is nothing to peek anymore`() {
                val testInstance = Tokenizer.DefaultImpl("h")
                assertThat(testInstance.peek(), equalTo('h'))
                testInstance.advance()
                val exception = assertThrows<IllegalStateException> {
                    testInstance.peek()
                }
                assertThat(exception.message, equalTo("Can not read after end of code"))
            }
        }

        @Nested
        @DisplayName("peekNext()")
        inner class PeekNextTest {
            @Test
            fun `Return the next character`() {
                val testInstance = Tokenizer.DefaultImpl("hamal")
                assertThat(testInstance.peekNext(), equalTo('a'))
                testInstance.advance()
                assertThat(testInstance.peekNext(), equalTo('m'))
            }

            @Test
            fun `Throws exception if there is nothing to peek anymore`() {
                val testInstance = Tokenizer.DefaultImpl("42")
                assertThat(testInstance.peekNext(), equalTo('2'))
                testInstance.advance()

                val exception = assertThrows<IllegalStateException> {
                    testInstance.peekNext()
                }
                assertThat(exception.message, equalTo("Can not read after end of code"))
            }
        }

        @Nested
        @DisplayName("peekPrev()")
        inner class PeekPrevTest {
            @Test
            fun `Returns the previous character`() {
                val testInstance = Tokenizer.DefaultImpl("hamal")
                testInstance.advance()
                assertThat(testInstance.peekPrev(), equalTo('h'))
                testInstance.advance()
                assertThat(testInstance.peekPrev(), equalTo('a'))
            }

            @Test
            fun `Throws exception if there is nothing to peek anymore`() {
                val testInstance = Tokenizer.DefaultImpl("h")
                testInstance.index = 2
                val exception = assertThrows<IllegalStateException> {
                    testInstance.peekPrev()
                }
                assertThat(exception.message, equalTo("Can not read after end of code"))
            }

            @Test
            fun `Throws exception if Tokenizer never advanced before`() {
                val testInstance = Tokenizer.DefaultImpl("hm")
                val exception = assertThrows<IllegalStateException> {
                    testInstance.peekPrev()
                }
                assertThat(exception.message, equalTo("Can not read before start of code"))
            }
        }

        @Nested
        @DisplayName("advance()")
        inner class AdvanceTest() {
            @Test
            fun `Advances read position and fills buffer`() {
                val testInstance = Tokenizer.DefaultImpl("num = 42")
                testInstance.advance().assert(1, 1, 1, "n")
                testInstance.advance().assert(2, 1, 2, "nu")
                testInstance.advance().assert(3, 1, 3, "num")
            }

            @Test
            fun `Throws exception if there is nothing to read anymore`() {
                val testInstance = Tokenizer.DefaultImpl("n")
                testInstance.advance().assert(1, 1, 1, "n")

                val exception = assertThrows<IllegalStateException> {
                    testInstance.advance()
                }
                assertThat(exception.message, equalTo("Can not read after end of code"))
            }
        }

        @Nested
        @DisplayName("advanceUntilWhitespace()")
        inner class AdvanceUntilWhitespaceTest {
            @Test
            fun `Advances until hits whitespace`() {
                val testInstance = Tokenizer.DefaultImpl("hamal rockz")
                testInstance.advanceUntilWhitespace()
                testInstance.assert(5, 1, 5, "hamal")
            }

            @Test
            fun `Advances until hits new line`() {
                val testInstance = Tokenizer.DefaultImpl("hamal\nrockz")
                testInstance.advanceUntilWhitespace()
                testInstance.assert(5, 1, 5, "hamal")
            }

            @Test
            fun `Advances until hits tabulator`() {
                val testInstance = Tokenizer.DefaultImpl("hamal\trockz")
                testInstance.advanceUntilWhitespace()
                testInstance.assert(5, 1, 5, "hamal")
            }

            @Test
            fun `Advances until hits carriage return`() {
                val testInstance = Tokenizer.DefaultImpl("hamal\rrockz")
                testInstance.advanceUntilWhitespace()
                testInstance.assert(5, 1, 5, "hamal")
            }
        }

        @Nested
        @DisplayName("skipWhitespace()")
        inner class SkipWhitespaceTest {
            @Test
            fun `Does nothing if not a whitespace`() {
                val testInstance = Tokenizer.DefaultImpl("hamal")
                testInstance.skipWhitespace().assert(0, 1, 0, "")
            }

            @Test
            fun `Skips comment`() {
                val testInstance = Tokenizer.DefaultImpl(
                    """
                    --- some comment on how awesome hamal is
                    invoke_awesomeness()
                """.trimIndent()
                )
                testInstance.skipWhitespace().assert(41, 2, 1, "")
            }

            @Test
            fun `Skips whitespaces`() {
                val testInstance = Tokenizer.DefaultImpl(
                    """    content"""
                )
                testInstance.skipWhitespace().assert(4, 1, 4, "")
            }

            @Test
            fun `Skips line breaks`() {
                val testInstance = Tokenizer.DefaultImpl(
                    "\n\n\ncontent"
                )
                testInstance.skipWhitespace().assert(3, 4, 1, "")
            }

            @Test
            fun `Skips tabulators breaks`() {
                val testInstance = Tokenizer.DefaultImpl(
                    "\t\t\tcontent"
                )
                testInstance.skipWhitespace().assert(3, 1, 3, "")
            }

            @Test
            fun `Skips carriage return breaks`() {
                val testInstance = Tokenizer.DefaultImpl(
                    "\r\rcontent"
                )
                testInstance.skipWhitespace().assert(2, 1, 2, "")
            }
        }

        @Nested
        @DisplayName("nextToken()")
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        inner class NextTokenTest {

            inner class Argument(val input: String, val expectedToken: Token) {
                override fun toString() = "Expect $input to be ${expectedToken.type}"
            }

            private fun arguments() = listOf(
                Argument(
                    "some_variable",
                    Token(Type.Identifier, TokenLine(1), TokenPosition(1), TokenValue("some_variable"))
                ),
                Argument("23.45", Token(Type.NumberLiteral, TokenLine(1), TokenPosition(1), TokenValue("23.45"))),
                Argument("0x815", Token(Type.HexNumberLiteral, TokenLine(1), TokenPosition(1), TokenValue("0x815"))),
                Argument(
                    "'Hello Hamal'",
                    Token(Type.StringLiteral, TokenLine(1), TokenPosition(1), TokenValue("Hello Hamal"))
                ),
                Argument("true", Token(Type.TrueLiteral, TokenLine(1), TokenPosition(1), TokenValue("true"))),
                Argument("false", Token(Type.FalseLiteral, TokenLine(1), TokenPosition(1), TokenValue("false"))),
                Argument("nil", Token(Type.NilLiteral, TokenLine(1), TokenPosition(1), TokenValue("nil"))),
                Argument("break", Token(Type.Break, TokenLine(1), TokenPosition(1), TokenValue("break"))),
                Argument("and", Token(Type.And, TokenLine(1), TokenPosition(1), TokenValue("and"))),
                Argument("do", Token(Type.Do, TokenLine(1), TokenPosition(1), TokenValue("do"))),
                Argument("else", Token(Type.Else, TokenLine(1), TokenPosition(1), TokenValue("else"))),
                Argument("elseif", Token(Type.ElseIf, TokenLine(1), TokenPosition(1), TokenValue("elseif"))),
                Argument("end", Token(Type.End, TokenLine(1), TokenPosition(1), TokenValue("end"))),
                Argument("for", Token(Type.For, TokenLine(1), TokenPosition(1), TokenValue("for"))),
                Argument("function", Token(Type.Function, TokenLine(1), TokenPosition(1), TokenValue("function"))),
                Argument("if", Token(Type.If, TokenLine(1), TokenPosition(1), TokenValue("if"))),
                Argument("in", Token(Type.In, TokenLine(1), TokenPosition(1), TokenValue("in"))),
                Argument("local", Token(Type.Local, TokenLine(1), TokenPosition(1), TokenValue("local"))),
                Argument("nor", Token(Type.Nor, TokenLine(1), TokenPosition(1), TokenValue("nor"))),
                Argument("not", Token(Type.Not, TokenLine(1), TokenPosition(1), TokenValue("not"))),
                Argument("or", Token(Type.Or, TokenLine(1), TokenPosition(1), TokenValue("or"))),
                Argument("repeat", Token(Type.Repeat, TokenLine(1), TokenPosition(1), TokenValue("repeat"))),
                Argument("return", Token(Type.Return, TokenLine(1), TokenPosition(1), TokenValue("return"))),
                Argument("then", Token(Type.Then, TokenLine(1), TokenPosition(1), TokenValue("then"))),
                Argument("until", Token(Type.Until, TokenLine(1), TokenPosition(1), TokenValue("until"))),
                Argument("while", Token(Type.While, TokenLine(1), TokenPosition(1), TokenValue("while"))),
                Argument("xor", Token(Type.Xor, TokenLine(1), TokenPosition(1), TokenValue("xor"))),

                Argument("*", Token(Type.Asterisk, TokenLine(1), TokenPosition(1), TokenValue("*"))),
                Argument("^", Token(Type.Carat, TokenLine(1), TokenPosition(1), TokenValue("^"))),
                Argument(":", Token(Type.Colon, TokenLine(1), TokenPosition(1), TokenValue(":"))),
                Argument(".", Token(Type.Dot, TokenLine(1), TokenPosition(1), TokenValue("."))),
                Argument("=", Token(Type.Equal, TokenLine(1), TokenPosition(1), TokenValue("="))),
                Argument("#", Token(Type.Hash, TokenLine(1), TokenPosition(1), TokenValue("#"))),
                Argument("<", Token(Type.LeftAngleBracket, TokenLine(1), TokenPosition(1), TokenValue("<"))),
                Argument("[", Token(Type.LeftBracket, TokenLine(1), TokenPosition(1), TokenValue("["))),
                Argument("-", Token(Type.Minus, TokenLine(1), TokenPosition(1), TokenValue("-"))),
                Argument("%", Token(Type.Percent, TokenLine(1), TokenPosition(1), TokenValue("%"))),
                Argument("+", Token(Type.Plus, TokenLine(1), TokenPosition(1), TokenValue("+"))),
                Argument(">", Token(Type.RightAngleBracket, TokenLine(1), TokenPosition(1), TokenValue(">"))),
                Argument("]", Token(Type.RightBracket, TokenLine(1), TokenPosition(1), TokenValue("]"))),
                Argument("/", Token(Type.Slash, TokenLine(1), TokenPosition(1), TokenValue("/"))),
                Argument("~", Token(Type.Tilde, TokenLine(1), TokenPosition(1), TokenValue("~"))),

                )

            @ParameterizedTest
            @MethodSource("arguments")
            fun test(argument: Argument) {
                val expected = argument.expectedToken
                val testInstance = Tokenizer.DefaultImpl(argument.input)
                val result = testInstance.nextToken()
                assertThat("Of type ${expected.type}", result.type, equalTo(expected.type))
                assertThat("Line ${expected.line}", result.line, equalTo(expected.line))
                assertThat("Position ${expected.position}", result.position, equalTo(expected.position))
                assertThat("Value ${expected.value}", result.value, equalTo(expected.value))

            }
        }

        @Nested
        @DisplayName("nextNumber()")
        inner class NextNumberTest {
            @Test
            fun `Single digit`() {
                val testInstance = Tokenizer.DefaultImpl("2")
                testInstance.nextNumber().assertNumberLiteral(1, 1, "2")
            }

            @Test
            fun `Multiple digits`() {
                val testInstance = Tokenizer.DefaultImpl("2345")
                testInstance.nextNumber().assertNumberLiteral(1, 1, "2345")
            }

            @Test
            fun `Floating point number`() {
                val testInstance = Tokenizer.DefaultImpl("13.37")
                testInstance.nextNumber().assertNumberLiteral(1, 1, "13.37")
            }

            @Test
            fun `Floating point number ends with dot`() {
                val testInstance = Tokenizer.DefaultImpl("13. ")
                testInstance.nextNumber().assertNumberLiteral(1, 1, "13.")
            }

            @Test
            fun `Floating point number starts with dot`() {
                val testInstance = Tokenizer.DefaultImpl(".234")
                testInstance.nextNumber().assertNumberLiteral(1, 1, ".234")
            }
        }

        @Nested
        @DisplayName("nextHexNumber()")
        inner class NextHexNumberTest {
            @Test
            fun `Hexnumber 0x0`() {
                val testInstance = Tokenizer.DefaultImpl("0x0")
                testInstance.nextHexNumber().assertHexNumberLiteral(1, 1, "0x0")
            }

            @Test
            fun `Hexnumber 0xBadC0de`() {
                val testInstance = Tokenizer.DefaultImpl("0xBadC0de")
                testInstance.nextHexNumber().assertHexNumberLiteral(1, 1, "0xBadC0de")
            }

            @Test
            fun `Hexnumber 0x123456789ABCDEF`() {
                val testInstance = Tokenizer.DefaultImpl("0x123456789ABCDEF")
                testInstance.nextHexNumber().assertHexNumberLiteral(1, 1, "0x123456789ABCDEF")
            }
        }

        @Nested
        @DisplayName("nextString()")
        inner class NextStringTest {
            @Test
            fun `Simple string`() {
                val testInstance = Tokenizer.DefaultImpl("'Hamal'")
                testInstance.nextString().assertStringLiteral(1, 1, "Hamal")

                val eof = testInstance.nextToken()
                assertThat(eof.type, equalTo(Type.Eof))
            }

            @Test
            fun `String contains whitespaces`() {
                val testInstance = Tokenizer.DefaultImpl("'Hamal\nh4m41 hamal\t cool'")
                testInstance.nextString().assertStringLiteral(1, 1, "Hamal\nh4m41 hamal\t cool")
            }

            @Test
            fun `String with inner quotes`() {
                val testInstance = Tokenizer.DefaultImpl("'hamal\\'hamal'")
                testInstance.nextString().assertStringLiteral(1, 1, "hamal\\'hamal")
            }

            @Test
            fun `String contains inner double quotes`() {
                val testInstance = Tokenizer.DefaultImpl("'hamal\"hamal'")
                testInstance.nextString().assertStringLiteral(1, 1, "hamal\"hamal")
            }

            @Test
            fun `String is not terminated`() {
                val testInstance = Tokenizer.DefaultImpl("'hamal")
                testInstance.nextString().assertError(1, 1, "Unterminated string")
            }

        }

        private fun Tokenizer.DefaultImpl.assert(index: Int, line: Int, linePosition: Int, buffer: String) {
            assertThat("index is not $index", this.index, equalTo(index))
            assertThat("line is not $line", this.line, equalTo(line))
            assertThat("line position is not $linePosition", this.linePosition, equalTo(linePosition))
            assertThat("buffer is not $buffer", this.buffer.toString(), equalTo(buffer))
        }

        private fun Token.assertNumberLiteral(line: Int, linePosition: Int, value: String) {
            assertLiteral(Type.NumberLiteral, line, linePosition, value)
        }

        private fun Token.assertHexNumberLiteral(line: Int, linePosition: Int, value: String) {
            assertLiteral(HexNumberLiteral, line, linePosition, value)
        }

        private fun Token.assertStringLiteral(line: Int, linePosition: Int, value: String) {
            assertLiteral(Type.StringLiteral, line, linePosition, value)
        }

        private fun Token.assertLiteral(type: Type, line: Int, linePosition: Int, value: String) {
            assertThat(this.type, equalTo(type))
            assertThat("line is not $line", this.line, equalTo(TokenLine(line)))
            assertThat("line position is not $linePosition", this.position, equalTo(TokenPosition(linePosition)))
            assertThat("value is not $value", this.value, equalTo(TokenValue(value)))
        }

        private fun Token.assertError(line: Int, linePosition: Int, value: String) {
            assertThat(type, equalTo(Error))
            assertThat("line is not $line", this.line, equalTo(TokenLine(line)))
            assertThat("line position is not $linePosition", this.position, equalTo(TokenPosition(linePosition)))
            assertThat("value is not $value", this.value, equalTo(TokenValue(value)))
        }
    }
}