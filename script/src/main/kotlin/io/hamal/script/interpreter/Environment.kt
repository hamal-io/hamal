package io.hamal.script.interpreter

import io.hamal.script.ForeignModule
import io.hamal.script.ast.expr.Identifier
import io.hamal.script.builtin.AssertFunction
import io.hamal.script.builtin.BuiltinFunction
import io.hamal.script.value.PrototypeValue
import io.hamal.script.value.StringValue
import io.hamal.script.value.Value

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