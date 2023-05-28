package io.hamal.lib.script.api.value

import io.hamal.lib.script.api.Context

interface DepFunctionValue : DepValue {
    val identifier: DepIdentifier //FIXME a signature might come handy here (name, parameter definition plus return)

    operator fun invoke(ctx: Context): DepValue

}