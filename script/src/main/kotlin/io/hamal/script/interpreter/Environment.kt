package io.hamal.script.interpreter

import io.hamal.script.ast.expr.Identifier
import io.hamal.script.builtin.AssertFunction
import io.hamal.script.builtin.ForeignFunction
import io.hamal.script.value.PrototypeValue
import io.hamal.script.value.StringValue
import io.hamal.script.value.Value

class Environment {

    private val parent: Environment? = null

    private val foreignFunctions = mutableMapOf<Identifier, ForeignFunction>(
        Identifier("assert") to AssertFunction
    )
    private val prototypes = mutableMapOf<StringValue, PrototypeValue>()

    fun assignLocal(identifier: StringValue, value: Value) {
        when (value) {
            is PrototypeValue -> prototypes[identifier] = value
            else -> TODO()
        }
    }

    fun findForeignFunction(identifier: Identifier) : ForeignFunction?{
        return foreignFunctions[identifier]
    }

    fun findPrototype(identifier: StringValue): PrototypeValue? {
        return prototypes[identifier]
    }

}