package io.hamal.agent.extension.api

import io.hamal.lib.script.api.value.EnvValue
import io.hamal.lib.script.api.value.FuncInvocationContext
import io.hamal.lib.script.api.value.FuncValue
import io.hamal.lib.script.api.value.Value

abstract class ExtensionFunc : FuncValue<ExtensionFuncInvocationContext>()
data class ExtensionFuncInvocationContext(
    override val parameters: List<Value>,
    override val env: EnvValue
) : FuncInvocationContext