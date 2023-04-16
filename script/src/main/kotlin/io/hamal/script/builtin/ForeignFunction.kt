package io.hamal.script.builtin

import io.hamal.script.ast.expr.Expression
import io.hamal.script.value.Value

interface ForeignFunction {
    operator fun invoke(ctx: Context): Value

    data class Context(
        val parameters: List<Parameter>
    )

    data class Parameter(
        val value: Value,
        val expression: Expression
    )
}