package io.hamal.lib.script.api

import io.hamal.lib.script.api.native_.NativeFunction
import io.hamal.lib.script.api.value.Identifier
import io.hamal.lib.script.api.value.Value

interface Environment : Value {
    val identifier: Identifier
    fun addLocal(identifier: Identifier, value: Value)
    fun addGlobal(identifier: Identifier, value: Value)
    operator fun get(identifier: Identifier): Value
    operator fun get(identifier: String): Value

    fun findNativeFunction(identifier: Identifier): NativeFunction?
    fun findEnvironment(identifier: Identifier): Environment?

}