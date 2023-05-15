package io.hamal.lib.script.api.native_

import io.hamal.lib.script.api.Environment
import io.hamal.lib.script.api.ast.Identifier
import io.hamal.lib.script.api.value.Value

class NativeEnvironment(
    override val identifier: Identifier,
    private val nativeFunctions: Map<Identifier, NativeFunction>
) : Environment {
    override fun assignLocal(identifier: Identifier, value: Value) {
        TODO("Not yet implemented")
    }

    override fun findNativeFunction(identifier: Identifier): NativeFunction? = nativeFunctions[identifier]
    override fun findEnvironment(identifier: Identifier): Environment? {
        TODO("Not yet implemented")
    }

}