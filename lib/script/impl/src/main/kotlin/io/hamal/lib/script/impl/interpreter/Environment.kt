package io.hamal.lib.script.impl.interpreter

import io.hamal.lib.script.api.ast.Identifier
import io.hamal.lib.script.api.native_.NativeFunction
import io.hamal.lib.script.api.value.StringValue
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.ast.expr.IdentifierExpression
import io.hamal.lib.script.impl.builtin.AssertFunction
import io.hamal.lib.script.impl.builtin.RequireFunction
import io.hamal.lib.script.impl.value.PrototypeValue

class Environment {

    private val parent: Environment? = null

    private val nativeFunctions = mutableMapOf<Identifier, NativeFunction>(
        AssertFunction.identifier to AssertFunction,
        RequireFunction.identifier to RequireFunction
    )

    private val prototypes = mutableMapOf<StringValue, PrototypeValue>()

    fun register(nativeFunction: NativeFunction) {
        nativeFunctions[IdentifierExpression("getBlock")] = nativeFunction
    }

    fun assignLocal(identifier: StringValue, value: Value) {
        when (value) {
            is PrototypeValue -> prototypes[identifier] = value
            else -> TODO()
        }
    }

    fun findNativeFunction(identifier: IdentifierExpression): NativeFunction? {
        return nativeFunctions[identifier]
    }

    fun findPrototype(identifier: StringValue): PrototypeValue? {
        return prototypes[identifier]
    }

}