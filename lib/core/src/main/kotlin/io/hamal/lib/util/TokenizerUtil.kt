package io.hamal.lib.util

object TokenizerUtil {

    fun isDigit(c: Char) = c.isDigit()

    fun isAlpha(c: Char) = (c in 'a'..'z') || (c in 'A'..'Z')

    fun isUnderscore(c: Char) = c == '_'

    fun isQuote(c: Char) = c == '\''

    fun isWhitespace(c: Char) = c == ' ' || c == '\t' || c == '\n' || c == '\r';

    fun isHexChar(c: Char) = isDigit(c) || (c in 'a'..'f') || (c in 'A'..'F');
}