package io.hamal.script.interpreter

import io.hamal.script.value.PrototypeValue
import io.hamal.script.value.StringValue
import io.hamal.script.value.Value

class Environment {

    private val parent: Environment? = null

    private val prototypes = mutableMapOf<StringValue, PrototypeValue>()

    fun assignLocal(identifier: StringValue, value: Value){
        when(value){
            is PrototypeValue -> prototypes[identifier] = value
            else -> TODO()
        }
    }

    fun findLocalPrototype(identifier: StringValue) : PrototypeValue?{
        return prototypes[identifier]
    }

}