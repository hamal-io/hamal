package io.hamal.lib.script.api

import io.hamal.lib.script.api.value.Value

interface NativeFunction {

    operator fun invoke(ctx: Context): Value

    data class Context(
        val parameters: List<Parameter>
    )

    data class Parameter(
        val value: Value,
        val expression: Expression
    )
}