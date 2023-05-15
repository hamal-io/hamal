package io.hamal.lib.script.api

import io.hamal.lib.script.api.ast.Identifier
import io.hamal.lib.script.api.native_.NativeFunction
import io.hamal.lib.script.api.value.Value

interface Environment : Value {
    val identifier: Identifier
    fun assignLocal(identifier: Identifier, value: Value)
    fun findNativeFunction(identifier: Identifier): NativeFunction?
    fun findEnvironment(identifier: Identifier): Environment?
}