package io.hamal.module.worker.script.token

import io.hamal.module.worker.script.token.Token.Type.Category.*

class Token(
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
        Nor("nor", Keyword),
        Not("not", Keyword),
        Or("or", Keyword),
        Repeat("repeat", Keyword),
        Return("return", Keyword),
        Then("then", Keyword),
        Until("until", Keyword),
        While("while", Keyword),
        Xor("xor", Keyword),

        Asterisk("*", Operator),
        Carat("^", Operator),
        Colon(":", Operator),
        Dot(".", Operator),
        Equal("=", Operator),
        Hash("#", Operator),
        LeftAngleBracket("<", Operator),
        LeftBracket("[", Operator),
        LeftParenthesis("(", Operator),
        Minus("-", Operator),
        Percent("%", Operator),
        Plus("+", Operator),
        RightAngleBracket(">", Operator),
        RightBracket("]", Operator),
        RightParenthesis(")", Operator),
        Slash("/", Operator),
        Tilde("~", Operator),

        Identifier("identifier", Category.Identifier),

        FalseLiteral("false", Literal),
        TrueLiteral("true", Literal),
        HexNumberLiteral("hex_number", Literal),
        NumberLiteral("number", Literal),
        NilLiteral("nil", Literal),
        StringLiteral("string", Literal),

        Semicolon(";", Delimiter),
        Comma(",", Delimiter),

        Error("error", Misc),
        Eof("eof", Misc);

        enum class Category {
            Delimiter,
            Keyword,
            Operator,
            Literal,
            Identifier,
            Misc,
        }
    }
}