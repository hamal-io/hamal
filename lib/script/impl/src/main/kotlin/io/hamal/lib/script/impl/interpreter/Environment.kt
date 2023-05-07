package io.hamal.lib.script.impl.interpreter

import io.hamal.lib.script.impl.ast.expr.Identifier
import io.hamal.lib.script.impl.builtin.AssertFunction
import io.hamal.lib.script.impl.builtin.BuiltinFunction
import io.hamal.lib.script.impl.value.PrototypeValue
import io.hamal.lib.script.api.ForeignModule
import io.hamal.lib.script.api.value.StringValue
import io.hamal.lib.script.api.value.Value

class Environment {

    private val parent: Environment? = null

    private val builtins = mutableMapOf<Identifier, BuiltinFunction>(
        Identifier("assert") to AssertFunction
    )

    val modules = mutableMapOf<String, ForeignModule>()

    private val prototypes = mutableMapOf<StringValue, PrototypeValue>()

    fun register(foreignModule: ForeignModule) {
        require(modules[foreignModule.name] == null)
        modules[foreignModule.name] = foreignModule
    }


    fun assignLocal(identifier: StringValue, value: Value) {
        when (value) {
            is PrototypeValue -> prototypes[identifier] = value
            else -> TODO()
        }
    }

    fun findForeignFunction(identifier: Identifier): BuiltinFunction? {
        return builtins[identifier]
    }

    fun findPrototype(identifier: StringValue): PrototypeValue? {
        return prototypes[identifier]
    }

}