package io.hamal.script.impl.builtin

import io.hamal.script.impl.ast.expr.Expression
import io.hamal.script.api.value.Value

interface BuiltinFunction {
    operator fun invoke(ctx: Context): Value

    data class Context(
        val parameters: List<Parameter>
    )

    data class Parameter(
        val value: Value,
        val expression: Expression
    )
}