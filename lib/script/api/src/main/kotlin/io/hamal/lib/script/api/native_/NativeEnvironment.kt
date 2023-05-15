package io.hamal.lib.script.api.native_

import io.hamal.lib.script.api.Environment
import io.hamal.lib.script.api.value.Identifier
import io.hamal.lib.script.api.value.Value

//FIXME maybe native environment should be readonly
class NativeEnvironment(
    override val identifier: Identifier,
    private val nativeFunctions: Map<Identifier, NativeFunction>
) : Environment {
    override fun addLocal(identifier: Identifier, value: Value) {
        TODO("Not yet implemented")
    }

    override fun addGlobal(identifier: Identifier, value: Value) {
        TODO("Not yet implemented")
    }

    override fun get(identifier: Identifier): Value {
        TODO("Not yet implemented")
    }

    override fun get(identifier: String): Value {
        TODO("Not yet implemented")
    }

    override fun findNativeFunction(identifier: Identifier): NativeFunction? = nativeFunctions[identifier]
    override fun findEnvironment(identifier: Identifier): Environment? {
        TODO("Not yet implemented")
    }

}