package io.hamal.lib.script.impl.interpreter

import io.hamal.lib.script.api.value.StringValue
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.ast.expr.Identifier
import io.hamal.lib.script.impl.builtin.AssertFunction
import io.hamal.lib.script.api.NativeFunction
import io.hamal.lib.script.impl.value.PrototypeValue

class Environment {

    private val parent: Environment? = null

    private val builtins = mutableMapOf<Identifier, NativeFunction>(
        Identifier("assert") to AssertFunction
    )

    private val prototypes = mutableMapOf<StringValue, PrototypeValue>()


    fun assignLocal(identifier: StringValue, value: Value) {
        when (value) {
            is PrototypeValue -> prototypes[identifier] = value
            else -> TODO()
        }
    }

    fun findForeignFunction(identifier: Identifier): NativeFunction? {
        return builtins[identifier]
    }

    fun findPrototype(identifier: StringValue): PrototypeValue? {
        return prototypes[identifier]
    }

}