package io.hamal.lib.script.api.native_

import io.hamal.lib.script.api.Environment
import io.hamal.lib.script.api.ast.Expression
import io.hamal.lib.script.api.ast.Identifier
import io.hamal.lib.script.api.value.Value

interface NativeFunction : Value {
    val identifier: Identifier //FIXME a signature might come handy here (name, parameter definition plus return)

    operator fun invoke(ctx: Context): Value

    data class Context(
        val parameters: List<Parameter>,
        val env: Environment
    )

    data class Parameter(
        val value: Value,
        val expression: Expression
    ) {
        fun asIdentifier(): Identifier {
            require(value is Identifier)
            return value
        }
    }
}