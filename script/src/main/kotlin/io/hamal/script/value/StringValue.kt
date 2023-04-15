package io.hamal.script.value

import io.hamal.script.ast.expr.Identifier

data class StringValue(val value: String) : Value {
    constructor(identifier: Identifier) : this(identifier.value)
    override fun toString() = "'$value'"
}