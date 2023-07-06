package io.hamal.lib.script.impl.token

import io.hamal.lib.script.impl.token.Token.Type.Category.*

data class Token(
    val type: Type,
    val line: Int,
    val position: Int,
    val value: String
) {

    override fun toString(): String {
        return "Token($type)"
    }

    enum class Type(val value: kotlin.String, val category: Category) {
        And("and", Keyword),
        Break("break", Keyword),
        Do("do", Keyword),
        Else("else", Keyword),
        ElseIf("elseif", Keyword),
        End("end", Keyword),
        For("for", Keyword),
        Function("function", Keyword),
        If("if", Keyword),
        In("in", Keyword),
        Local("local", Keyword),
        Not("not", Keyword),
        Or("or", Keyword),
        Repeat("repeat", Keyword),
        Return("return", Keyword),
        Then("then", Keyword),
        Until("until", Keyword),
        While("while", Keyword),

        Asterisk("*", Operator),
        Carat("^", Operator),
        Colon(":", Operator),
        Dot(".", Operator),
        Dot_Dot("..", Operator),
        Equal("=", Operator),
        Equal_Equal("==", Operator),
        Hash("#", Operator),
        LeftAngleBracket("<", Operator),
        LeftAngleBracket_Equal("<=", Operator),
        LeftAngleBracket_LeftAngleBracket("<<", Operator),
        LeftBracket("[", Operator),
        LeftCurlyBracket("{", Operator),
        LeftParenthesis("(", Operator),
        Minus("-", Operator),
        Percent("%", Operator),
        Plus("+", Operator),
        RightAngleBracket(">", Operator),
        RightAngleBracket_Equal(">=", Operator),
        RightAngleBracket_RightAngleBracket(">>", Operator),
        RightBracket("]", Operator),
        RightCurlyBracket("}", Operator),
        RightParenthesis(")", Operator),
        Slash("/", Operator),
        Tilde("~", Operator),
        Tilde_Equal("~=", Operator),

        Ident("ident", Category.Ident),

        False("false", Literal),
        True("true", Literal),
        HexNumber("hex_number", Literal),
        Number("number", Literal),
        Nil("nil", Literal),
        String("string", Literal),
        Code("code", Literal),

        Semicolon(";", Delimiter),
        Comma(",", Delimiter),

        Error("error", Misc),
        Eof("eof", Misc);

        enum class Category {
            Delimiter,
            Keyword,
            Operator,
            Literal,
            Ident,
            Misc,
        }

        override fun toString() = value
    }
}