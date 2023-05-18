package io.hamal.lib.script.api

import io.hamal.lib.script.api.value.Identifier
import io.hamal.lib.script.api.value.MetaTableNotImplementedYet
import io.hamal.lib.script.api.value.Value

//FIXME maybe native environment should be readonly
class NativeEnvironment(
    override val identifier: Identifier,
    private val values: Map<Identifier, Value>
) : Environment {
    override val metaTable = MetaTableNotImplementedYet

    override fun addLocal(identifier: Identifier, value: Value) {
        TODO("Not yet implemented")
    }

    override fun addGlobal(identifier: Identifier, value: Value) {
        TODO("Not yet implemented")
    }

    override fun get(identifier: Identifier): Value {
        return requireNotNull(values[identifier])
    }

    override fun findNativeFunction(identifier: Identifier): FunctionValue? =
        values[identifier]?.let { it as FunctionValue }

    override fun findEnvironment(identifier: Identifier): Environment? {
        return values[identifier]?.let { it as Environment }
    }

    override fun toString(): String {
        return "$identifier{$values}"
    }

}