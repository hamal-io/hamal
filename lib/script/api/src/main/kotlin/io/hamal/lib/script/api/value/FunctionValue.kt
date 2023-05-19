package io.hamal.lib.script.api.value

import io.hamal.lib.script.api.Context

interface FunctionValue : Value {
    val identifier: Identifier //FIXME a signature might come handy here (name, parameter definition plus return)

    operator fun invoke(ctx: Context): Value

}