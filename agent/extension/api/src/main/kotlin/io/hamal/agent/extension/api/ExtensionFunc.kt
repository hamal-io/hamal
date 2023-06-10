package io.hamal.agent.extension.api

import io.hamal.lib.script.api.value.EnvValue
import io.hamal.lib.script.api.value.FuncValue
import io.hamal.lib.script.api.value.Value

abstract class ExtensionFunc : FuncValue<ExtensionFunc.Context>() {
    data class Context(
        override val parameters: List<Value>,
        override val env: EnvValue
    ) : FuncValue.Context
}