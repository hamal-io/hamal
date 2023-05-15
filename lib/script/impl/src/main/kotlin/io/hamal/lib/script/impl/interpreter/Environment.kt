package io.hamal.lib.script.impl.interpreter

import io.hamal.lib.script.api.Environment
import io.hamal.lib.script.api.ast.Identifier
import io.hamal.lib.script.api.native_.NativeFunction
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.ast.expr.IdentifierLiteral
import io.hamal.lib.script.impl.builtin.AssertFunction
import io.hamal.lib.script.impl.builtin.RequireFunction
import io.hamal.lib.script.impl.value.PrototypeValue

class RootEnvironment : Environment {

    private val parent: Environment? = null

    private val nativeFunctions = mutableMapOf<Identifier, NativeFunction>(
        AssertFunction.identifier to AssertFunction,
        RequireFunction.identifier to RequireFunction
    )

    private val extensions = mutableMapOf<Identifier, Environment>()

    private val prototypes = mutableMapOf<Identifier, PrototypeValue>()

    fun register(nativeFunction: NativeFunction) {
        nativeFunctions[IdentifierLiteral("getBlock")] = nativeFunction
    }

    override val identifier: Identifier
        get() = IdentifierLiteral("root")

    override fun findNativeFunction(identifier: Identifier): NativeFunction? {
        TODO("Not yet implemented")
    }

    override fun findEnvironment(identifier: Identifier): Environment? {
        return extensions[identifier]
    }

    fun register(environment: Environment) {
        extensions[environment.identifier] = environment
    }

    override fun assignLocal(identifier: Identifier, value: Value) {
        when (value) {
            is PrototypeValue -> prototypes[identifier] = value
            else -> TODO()
        }
    }

    fun findNativeFunction(identifier: IdentifierLiteral): NativeFunction? {
        return nativeFunctions[identifier]
    }

    fun findPrototype(identifier: Identifier): PrototypeValue? {
        return prototypes[identifier]
    }

}