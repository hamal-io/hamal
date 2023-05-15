package io.hamal.lib.script.impl.interpreter

import io.hamal.lib.script.api.Environment
import io.hamal.lib.script.api.native_.NativeFunction
import io.hamal.lib.script.api.value.Identifier
import io.hamal.lib.script.api.value.NilValue
import io.hamal.lib.script.api.value.NumberValue
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.builtin.AssertFunction
import io.hamal.lib.script.impl.builtin.RequireFunction
import io.hamal.lib.script.impl.value.PrototypeValue

class RootEnvironment : Environment {

    private val parent: Environment? = null

    private val nativeFunctions = mutableMapOf<Identifier, NativeFunction>(
        AssertFunction.identifier to AssertFunction, RequireFunction.identifier to RequireFunction
    )

    private val extensions = mutableMapOf<Identifier, Environment>()

    private val prototypes = mutableMapOf<Identifier, PrototypeValue>()

    // FIXME local and global should be one map - entry indicates whether this is local or global
    private val locals = mutableMapOf<Identifier, Value>()
    private val globals = mutableMapOf<Identifier, Value>()

    fun add(nativeFunction: NativeFunction) {
        nativeFunctions[Identifier("getBlock")] = nativeFunction
    }

    override val identifier: Identifier
        get() = Identifier("root")

    override fun findNativeFunction(identifier: Identifier): NativeFunction? {
        return nativeFunctions[identifier]
    }

    override fun findEnvironment(identifier: Identifier): Environment? {
        return extensions[identifier]
    }

    fun add(environment: Environment) {
        extensions[environment.identifier] = environment
    }

    override fun addLocal(identifier: Identifier, value: Value) {
        when (value) {
            is PrototypeValue -> prototypes[identifier] = value
            is NilValue -> locals[identifier] = value
            is NumberValue -> locals[identifier] = value
            else -> TODO()
        }
    }

    override fun addGlobal(identifier: Identifier, value: Value) {
        globals[identifier] = value
    }


    override fun get(identifier: Identifier): Value {
        return requireNotNull(locals[identifier] ?: globals[identifier])
    }

    override fun get(identifier: String): Value {
        return this[Identifier(identifier)]
    }

    fun findPrototype(identifier: Identifier): PrototypeValue? {
        return prototypes[identifier]
    }

}