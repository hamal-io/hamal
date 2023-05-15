package io.hamal.lib.script.api.native_

import io.hamal.lib.script.api.Environment
import io.hamal.lib.script.api.ast.Identifier

class NativeEnvironment(
    override val identifier: Identifier,
    private val nativeFunctions: Map<Identifier, NativeFunction>
) : Environment {

    override fun findNativeFunction(identifier: Identifier): NativeFunction? = nativeFunctions[identifier]
    override fun findEnvironment(identifier: Identifier): Environment? {
        TODO("Not yet implemented")
    }

}