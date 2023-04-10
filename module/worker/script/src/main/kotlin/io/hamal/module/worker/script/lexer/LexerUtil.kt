package io.hamal.module.worker.script.lexer

internal object LexerUtil {

    fun isDigit(c: Char) = c.isDigit()

    fun isAlpha(c: Char) = (c in 'a'..'z') || (c in 'A'..'Z')

    fun isUnderscore(c: Char) = c == '_'

    fun isWhitespace(c: Char) = c == ' ' || c == '\t' || c == '\n' || c == '\r';

    fun isHexChar(c: Char) = isDigit(c) || (c in 'a'..'f') || (c in 'A'..'F');
}