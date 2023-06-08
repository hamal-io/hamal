package io.hamal.lib.script.impl.token

import io.hamal.lib.script.impl.token.Token.Type
import io.hamal.lib.script.impl.token.Token.Type.*
import io.hamal.lib.script.impl.token.Token.Type.Function
import io.hamal.lib.script.impl.token.Token.Type.Number
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows


class TokenizerTest {
    @Nested
    inner class DefaultTokenizerTest {
        @Nested
        inner class IsAtEndTest {
            @Test
            fun `Advances until reaches end`() {
                val testInstance = DefaultTokenizer("num")
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
        inner class PeekTest {
            @Test
            fun `Returns the character of the current position`() {
                val testInstance = DefaultTokenizer("hamal")
                assertThat(testInstance.peek(), equalTo('h'))
                testInstance.advance()
                assertThat(testInstance.peek(), equalTo('a'))
            }

            @Test
            fun `Throws exception if there is nothing to peek anymore`() {
                val testInstance = DefaultTokenizer("h")
                assertThat(testInstance.peek(), equalTo('h'))
                testInstance.advance()
                val exception = assertThrows<IllegalStateException> {
                    testInstance.peek()
                }
                assertThat(exception.message, containsString("Can not read after end of code"))
            }
        }

        @Nested
        inner class PeekNextTest {
            @Test
            fun `Return the next character`() {
                val testInstance = DefaultTokenizer("hamal")
                assertThat(testInstance.peekNext(), equalTo('a'))
                testInstance.advance()
                assertThat(testInstance.peekNext(), equalTo('m'))
            }

            @Test
            fun `Throws exception if there is nothing to peek anymore`() {
                val testInstance = DefaultTokenizer("42")
                assertThat(testInstance.peekNext(), equalTo('2'))
                testInstance.advance()

                val exception = assertThrows<IllegalStateException> {
                    testInstance.peekNext()
                }
                assertThat(exception.message, containsString("Can not read after end of code"))
            }
        }

        @Nested
        inner class PeekPrevTest {
            @Test
            fun `Returns the previous character`() {
                val testInstance = DefaultTokenizer("hamal")
                testInstance.advance()
                assertThat(testInstance.peekPrev(), equalTo('h'))
                testInstance.advance()
                assertThat(testInstance.peekPrev(), equalTo('a'))
            }

            @Test
            fun `Throws exception if there is nothing to peek anymore`() {
                val testInstance = DefaultTokenizer("h")
                testInstance.index = 2
                val exception = assertThrows<IllegalStateException> {
                    testInstance.peekPrev()
                }
                assertThat(exception.message, containsString("Can not read after end of code"))
            }

            @Test
            fun `Throws exception if Tokenizer never advanced before`() {
                val testInstance = DefaultTokenizer("hm")
                val exception = assertThrows<IllegalStateException> {
                    testInstance.peekPrev()
                }
                assertThat(exception.message, containsString("Can not read before start of code"))
            }
        }

        @Nested
        inner class AdvanceTest {
            @Test
            fun `Advances read position and fills buffer`() {
                val testInstance = DefaultTokenizer("num = 42")
                testInstance.advance().assert(1, 1, 1, "n")
                testInstance.advance().assert(2, 1, 2, "nu")
                testInstance.advance().assert(3, 1, 3, "num")
            }

            @Test
            fun `Throws exception if there is nothing to read anymore`() {
                val testInstance = DefaultTokenizer("n")
                testInstance.advance().assert(1, 1, 1, "n")

                val exception = assertThrows<IllegalStateException> {
                    testInstance.advance()
                }
                assertThat(exception.message, containsString("Can not read after end of code"))
            }
        }

        @Nested
        inner class AdvanceUntilWhitespaceTest {
            @Test
            fun `Advances until hits whitespace`() {
                val testInstance = DefaultTokenizer("hamal rockz")
                testInstance.advanceUntilWhitespace()
                testInstance.assert(5, 1, 5, "hamal")
            }

            @Test
            fun `Advances until hits new line`() {
                val testInstance = DefaultTokenizer("hamal\nrockz")
                testInstance.advanceUntilWhitespace()
                testInstance.assert(5, 1, 5, "hamal")
            }

            @Test
            fun `Advances until hits tabulator`() {
                val testInstance = DefaultTokenizer("hamal\trockz")
                testInstance.advanceUntilWhitespace()
                testInstance.assert(5, 1, 5, "hamal")
            }

            @Test
            fun `Advances until hits carriage return`() {
                val testInstance = DefaultTokenizer("hamal\rrockz")
                testInstance.advanceUntilWhitespace()
                testInstance.assert(5, 1, 5, "hamal")
            }
        }

        @Nested
        inner class SkipWhitespaceTest {
            @Test
            fun `Does nothing if not a whitespace`() {
                val testInstance = DefaultTokenizer("hamal")
                testInstance.skipWhitespace().assert(0, 1, 0, "")
            }

            @Test
            fun `Skips comment`() {
                val testInstance = DefaultTokenizer(
                    """
                    --- some comment on how awesome hamal is
                    invoke_awesomeness()
                """.trimIndent()
                )
                testInstance.skipWhitespace().assert(41, 2, 1, "")
            }

            @Test
            fun `Skips whitespaces`() {
                val testInstance = DefaultTokenizer(
                    """    content"""
                )
                testInstance.skipWhitespace().assert(4, 1, 4, "")
            }

            @Test
            fun `Skips tabulators breaks`() {
                val testInstance = DefaultTokenizer(
                    "\t\t\tcontent"
                )
                testInstance.skipWhitespace().assert(3, 1, 3, "")
            }

            @Test
            fun `Skips carriage return breaks`() {
                val testInstance = DefaultTokenizer(
                    "\r\rcontent"
                )
                testInstance.skipWhitespace().assert(2, 1, 2, "")
            }
        }

        @Nested
        inner class NextTokenTest {

            @TestFactory
            fun tokens() = listOf(
                "some_variable" to Token(Identifier, 1, 1, "some_variable"),
                "not_variable" to Token(Identifier, 1, 1, "not_variable"),
                "and_variable" to Token(Identifier, 1, 1, "and_variable"),
                "or_variable" to Token(Identifier, 1, 1, "or_variable"),
                "true_variable" to Token(Identifier, 1, 1, "true_variable"),
                "23.45" to Token(Number, 1, 1, "23.45"),
                "0x815" to Token(HexNumber, 1, 1, "0x815"),

                "'Hello Hamal'" to Token(Type.String, 1, 1, "Hello Hamal"),
                "true" to Token(True, 1, 1, "true"),
                "false" to Token(False, 1, 1, "false"),
                "nil" to Token(Nil, 1, 1, "nil"),
                "break" to Token(Break, 1, 1, "break"),
                "do" to Token(Do, 1, 1, "do"),
                "else" to Token(Else, 1, 1, "else"),
                "elseif" to Token(ElseIf, 1, 1, "elseif"),
                "end" to Token(End, 1, 1, "end"),
                "for" to Token(For, 1, 1, "for"),
                "function" to Token(Function, 1, 1, "function"),
                "if" to Token(If, 1, 1, "if"),
                "in" to Token(In, 1, 1, "in"),
                "local" to Token(Local, 1, 1, "local"),
                "repeat" to Token(Repeat, 1, 1, "repeat"),
                "return" to Token(Return, 1, 1, "return"),
                "then" to Token(Then, 1, 1, "then"),
                "until" to Token(Until, 1, 1, "until"),
                "while" to Token(While, 1, 1, "while"),

                "and" to Token(And, 1, 1, "and"),
                "*" to Token(Asterisk, 1, 1, "*"),
                "^" to Token(Carat, 1, 1, "^"),
                ":" to Token(Colon, 1, 1, ":"),
                "." to Token(Dot, 1, 1, "."),
                ".." to Token(Dot_Dot, 1, 1, ".."),
                "=" to Token(Equal, 1, 1, "="),
                "==" to Token(Equal_Equal, 1, 1, "=="),
                "#" to Token(Hash, 1, 1, "#"),
                "<" to Token(LeftAngleBracket, 1, 1, "<"),
                "<=" to Token(LeftAngleBracket_Equal, 1, 1, "<="),
                "<<" to Token(LeftAngleBracket_LeftAngleBracket, 1, 1, "<<"),
                "[" to Token(LeftBracket, 1, 1, "["),
                "{" to Token(LeftCurlyBracket, 1, 1, "{"),

                "(" to Token(LeftParenthesis, 1, 1, "("),
                "-" to Token(Minus, 1, 1, "-"),
                "not" to Token(Not, 1, 1, "not"),
                "or" to Token(Or, 1, 1, "or"),
                "%" to Token(Percent, 1, 1, "%"),
                "+" to Token(Plus, 1, 1, "+"),
                ">" to Token(RightAngleBracket, 1, 1, ">"),
                ">=" to Token(RightAngleBracket_Equal, 1, 1, ">="),
                ">>" to Token(RightAngleBracket_RightAngleBracket, 1, 1, ">>"),
                "]" to Token(RightBracket, 1, 1, "]"),
                "}" to Token(RightCurlyBracket, 1, 1, "}"),
                ")" to Token(RightParenthesis, 1, 1, ")"),
                "/" to Token(Slash, 1, 1, "/"),
                "~" to Token(Tilde, 1, 1, "~"),
                "~=" to Token(Tilde_Equal, 1, 1, "~="),

                ";" to Token(Semicolon, 1, 1, ";"),
                "," to Token(Comma, 1, 1, ","),
            ).map { (code, expected) ->
                dynamicTest(code) {
                    val testInstance = DefaultTokenizer(code)
                    val result = testInstance.nextToken()
                    assertThat("Of type ${expected.type}", result.type, equalTo(expected.type))
                    assertThat("Line ${expected.line}", result.line, equalTo(expected.line))
                    assertThat("Position ${expected.position}", result.position, equalTo(expected.position))
                    assertThat("Value ${expected.value}", result.value, equalTo(expected.value))

                    assertThat(testInstance.nextToken().type, equalTo(Eof))
                }
            }

            @Test
            fun `Tokenize function call`() {
                val testInstance = DefaultTokenizer("some_function()")

                val identifier = testInstance.nextToken()
                assertThat(identifier.type, equalTo(Identifier))
                assertThat(identifier.value, equalTo("some_function"))

                val leftParenthesis = testInstance.nextToken()
                assertThat(leftParenthesis.type, equalTo(LeftParenthesis))

                val rightParenthesis = testInstance.nextToken()
                assertThat(rightParenthesis.type, equalTo(RightParenthesis))

                val eof = testInstance.nextToken()
                assertThat(eof.type, equalTo(Eof))
            }

            @Test
            fun `Tokenize a = {x=0, y=0}`() {
                val testInstance = DefaultTokenizer("a = {x=0, y=0}")

                var identifier = testInstance.nextToken()
                assertThat(identifier.type, equalTo(Identifier))
                assertThat(identifier.value, equalTo("a"))

                var equal = testInstance.nextToken()
                assertThat(equal.type, equalTo(Equal))
                assertThat(equal.value, equalTo("="))

                val leftCurly = testInstance.nextToken()
                assertThat(leftCurly.type, equalTo(LeftCurlyBracket))
                assertThat(leftCurly.value, equalTo("{"))

                identifier = testInstance.nextToken()
                assertThat(identifier.type, equalTo(Identifier))
                assertThat(identifier.value, equalTo("x"))

                equal = testInstance.nextToken()
                assertThat(equal.type, equalTo(Equal))
                assertThat(equal.value, equalTo("="))

                var number = testInstance.nextToken()
                assertThat(number.type, equalTo(Number))
                assertThat(number.value, equalTo("0"))

                val comma = testInstance.nextToken()
                assertThat(comma.type, equalTo(Comma))
                assertThat(comma.value, equalTo(","))

                identifier = testInstance.nextToken()
                assertThat(identifier.type, equalTo(Identifier))
                assertThat(identifier.value, equalTo("y"))

                equal = testInstance.nextToken()
                assertThat(equal.type, equalTo(Equal))
                assertThat(equal.value, equalTo("="))

                number = testInstance.nextToken()
                assertThat(number.type, equalTo(Number))
                assertThat(number.value, equalTo("0"))

                val rightCurly = testInstance.nextToken()
                assertThat(rightCurly.type, equalTo(RightCurlyBracket))
                assertThat(rightCurly.value, equalTo("}"))

            }
        }

        @Nested
        inner class NextNumberTest {
            @Test
            fun `Single digit`() {
                val testInstance = DefaultTokenizer("2")
                testInstance.nextNumber().assertNumberLiteral(1, 1, "2")
            }

            @Test
            fun `Multiple digits`() {
                val testInstance = DefaultTokenizer("2345")
                testInstance.nextNumber().assertNumberLiteral(1, 1, "2345")
            }

            @Test
            fun `Floating point number`() {
                val testInstance = DefaultTokenizer("13.37")
                testInstance.nextNumber().assertNumberLiteral(1, 1, "13.37")
            }

            @Test
            fun `Floating point number ends with dot`() {
                val testInstance = DefaultTokenizer("13. ")
                testInstance.nextNumber().assertNumberLiteral(1, 1, "13.")
            }

            @Test
            fun `Floating point number starts with dot`() {
                val testInstance = DefaultTokenizer(".234")
                testInstance.nextNumber().assertNumberLiteral(1, 1, ".234")
            }
        }

        @Nested
        inner class NextHexNumberTest {
            @Test
            fun `Hexnumber 0x0`() {
                val testInstance = DefaultTokenizer("0x0")
                testInstance.nextHexNumber().assertHexNumberLiteral(1, 1, "0x0")
            }

            @Test
            fun `Hexnumber 0xBadC0de`() {
                val testInstance = DefaultTokenizer("0xBadC0de")
                testInstance.nextHexNumber().assertHexNumberLiteral(1, 1, "0xBadC0de")
            }

            @Test
            fun `Hexnumber 0x123456789ABCDEF`() {
                val testInstance = DefaultTokenizer("0x123456789ABCDEF")
                testInstance.nextHexNumber().assertHexNumberLiteral(1, 1, "0x123456789ABCDEF")
            }
        }

        @Nested
        inner class NextStringTest {
            @Test
            fun `Simple string`() {
                val testInstance = DefaultTokenizer("'Hamal'")
                testInstance.nextString().assertStringLiteral(1, 1, "Hamal")

                val eof = testInstance.nextToken()
                assertThat(eof.type, equalTo(Eof))
            }

            @Test
            fun `String contains whitespaces`() {
                val testInstance = DefaultTokenizer("'Hamal\nh4m41 hamal\t cool'")
                testInstance.nextString().assertStringLiteral(1, 1, "Hamal\nh4m41 hamal\t cool")
            }

            @Test
            fun `String with inner quotes`() {
                val testInstance = DefaultTokenizer("'hamal\\'hamal'")
                testInstance.nextString().assertStringLiteral(1, 1, "hamal\\'hamal")
            }

            @Test
            fun `String contains inner double quotes`() {
                val testInstance = DefaultTokenizer("'hamal\"hamal'")
                testInstance.nextString().assertStringLiteral(1, 1, "hamal\"hamal")
            }

            @Test
            fun `String is not terminated`() {
                val testInstance = DefaultTokenizer("'hamal")
                testInstance.nextString().assertError(1, 1, "Unterminated string")
            }

        }

        @Test
        fun `a dot dot b`() {
            val testInstance = DefaultTokenizer("a..b")
            val a = testInstance.nextToken()
            assertThat(a, equalTo(Token(Identifier, 1, 1, "a")))
            val op = testInstance.nextToken()
            assertThat(op, equalTo(Token(Dot_Dot, 1, 2, "..")))
            val b = testInstance.nextToken()
            assertThat(b, equalTo(Token(Identifier, 1, 4, "b")))

        }

        @Test
        fun `some_number=2810`() {
            val testInstance = DefaultTokenizer("some_number=2810")
            val ident = testInstance.nextToken()
            assertThat(ident, equalTo(Token(Identifier, 1, 1, "some_number")))
            val equal = testInstance.nextToken()
            assertThat(equal, equalTo(Token(Equal, 1, 12, "=")))
            val number = testInstance.nextToken()
            assertThat(number, equalTo(Token(Number, 1, 13, "2810")))
        }

        @Test
        fun `some_number==1212`() {
            val testInstance = DefaultTokenizer("some_number==1212")
            val ident = testInstance.nextToken()
            assertThat(ident, equalTo(Token(Identifier, 1, 1, "some_number")))
            val equal = testInstance.nextToken()
            assertThat(equal, equalTo(Token(Equal_Equal, 1, 12, "==")))
            val number = testInstance.nextToken()
            assertThat(number, equalTo(Token(Number, 1, 14, "1212")))
        }

        @Test
        fun `some_number==another_number`() {
            val testInstance = DefaultTokenizer("some_number==another_number")
            val ident = testInstance.nextToken()
            assertThat(ident, equalTo(Token(Identifier, 1, 1, "some_number")))
            val equal = testInstance.nextToken()
            assertThat(equal, equalTo(Token(Equal_Equal, 1, 12, "==")))
            val number = testInstance.nextToken()
            assertThat(number, equalTo(Token(Identifier, 1, 14, "another_number")))
        }

        @Test
        fun `some_table dot field`() {
            val testInstance = DefaultTokenizer("some_table.field")
            val table = testInstance.nextToken()
            assertThat(table, equalTo(Token(Identifier, 1, 1, "some_table")))
        }

        @Test
        fun `local web3 = require('web3')`() {
            val testInstance = DefaultTokenizer("local eth = require('web3')")
            assertThat(testInstance.nextToken(), equalTo(Token(Local, 1, 1, "local")))
            assertThat(testInstance.nextToken(), equalTo(Token(Identifier, 1, 7, "eth")))
            assertThat(testInstance.nextToken(), equalTo(Token(Equal, 1, 11, "=")))
            assertThat(testInstance.nextToken(), equalTo(Token(Identifier, 1, 13, "require")))
            assertThat(testInstance.nextToken(), equalTo(Token(LeftParenthesis, 1, 20, "(")))
            assertThat(testInstance.nextToken(), equalTo(Token(Type.String, 1, 21, "web3")))
            assertThat(testInstance.nextToken(), equalTo(Token(RightParenthesis, 1, 27, ")")))
        }

        @Test
        fun `Empty code`() {
            val testInstance = DefaultTokenizer("<[]>")
            assertThat(testInstance.nextToken(), equalTo(Token(Code, 1, 1, "")))
        }

        @Test
        fun `Code is simple number test`() {
            val testInstance = DefaultTokenizer("<[ 42 ]>")
            assertThat(testInstance.nextToken(), equalTo(Token(Code, 1, 1, " 42 ")))
        }

        @Test
        fun `Code is mutlti line test`() {
            val testInstance = DefaultTokenizer(
                """<[
                |local log = require('log')
                |log.info('test')
                |]>""".trimMargin()
            )
            val result = testInstance.nextToken()
            assertThat(result, equalTo(Token(Code, 1, 1, """
local log = require('log')
log.info('test')
""")))
        }


        @Test
        fun ifStatement() {
            val testInstance = DefaultTokenizer("if a<0 then a = 0 end")
            assertThat(testInstance.nextToken(), equalTo(Token(If, 1, 1, "if")))
            assertThat(testInstance.nextToken(), equalTo(Token(Identifier, 1, 4, "a")))
            assertThat(testInstance.nextToken(), equalTo(Token(LeftAngleBracket, 1, 5, "<")))
            assertThat(testInstance.nextToken(), equalTo(Token(Number, 1, 6, "0")))
            assertThat(testInstance.nextToken(), equalTo(Token(Then, 1, 8, "then")))
            assertThat(testInstance.nextToken(), equalTo(Token(Identifier, 1, 13, "a")))
            assertThat(testInstance.nextToken(), equalTo(Token(Equal, 1, 15, "=")))
            assertThat(testInstance.nextToken(), equalTo(Token(Number, 1, 17, "0")))
            assertThat(testInstance.nextToken(), equalTo(Token(End, 1, 19, "end")))
        }

        private fun DefaultTokenizer.assert(index: Int, line: Int, linePosition: Int, buffer: String) {
            assertThat("index is not $index", this.index, equalTo(index))
            assertThat("line is not $line", this.line, equalTo(line))
            assertThat("line position is not $linePosition", this.linePosition, equalTo(linePosition))
            assertThat("buffer is not $buffer", this.buffer.toString(), equalTo(buffer))
        }

        private fun Token.assertNumberLiteral(line: Int, linePosition: Int, value: String) {
            assertLiteral(Number, line, linePosition, value)
        }

        private fun Token.assertHexNumberLiteral(line: Int, linePosition: Int, value: String) {
            assertLiteral(HexNumber, line, linePosition, value)
        }

        private fun Token.assertStringLiteral(line: Int, linePosition: Int, value: String) {
            assertLiteral(Type.String, line, linePosition, value)
        }

        private fun Token.assertLiteral(type: Type, line: Int, linePosition: Int, value: String) {
            assertThat(this.type, equalTo(type))
            assertThat("line is not $line", this.line, equalTo((line)))
            assertThat("line position is not $linePosition", this.position, equalTo((linePosition)))
            assertThat("value is not $value", this.value, equalTo((value)))
        }

        private fun Token.assertError(line: Int, linePosition: Int, value: String) {
            assertThat(type, equalTo(Error))
            assertThat("line is not $line", this.line, equalTo((line)))
            assertThat("line position is not $linePosition", this.position, equalTo((linePosition)))
            assertThat("value is not $value", this.value, equalTo((value)))
        }
    }
}