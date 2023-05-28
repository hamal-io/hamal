package io.hamal.lib.script.api

import io.hamal.lib.script.api.ast.Expression
import io.hamal.lib.script.api.value.DepEnvironmentValue
import io.hamal.lib.script.api.value.DepIdentifier
import io.hamal.lib.script.api.value.DepStringValue
import io.hamal.lib.script.api.value.DepValue

data class Context(
    val parameters: List<Parameter>,
    val env: DepEnvironmentValue
)

data class Parameter(
    val value: DepValue,
    val expression: Expression
) {
    fun asIdentifier(): DepIdentifier {
        return when (value) {
            is DepStringValue -> DepIdentifier(value = value.value)
            is DepIdentifier -> value
            else -> throw IllegalStateException("$value can not interpreted as identifier")
        }
    }
}