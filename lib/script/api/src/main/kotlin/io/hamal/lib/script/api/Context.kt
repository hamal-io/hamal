package io.hamal.lib.script.api

import io.hamal.lib.script.api.ast.Expression
import io.hamal.lib.script.api.value.EnvironmentValue
import io.hamal.lib.script.api.value.Identifier
import io.hamal.lib.script.api.value.StringValue
import io.hamal.lib.script.api.value.Value

data class Context(
    val parameters: List<Parameter>,
    val env: EnvironmentValue
)

data class Parameter(
    val value: Value,
    val expression: Expression
) {
    fun asIdentifier(): Identifier {
        return when (value) {
            is StringValue -> Identifier(value = value.value)
            is Identifier -> value
            else -> throw IllegalStateException("$value can not interpreted as identifier")
        }
    }
}