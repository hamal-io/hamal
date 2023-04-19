package io.hamal.script

import io.hamal.script.value.TableValue
import io.hamal.script.value.Value

abstract class ForeignFunction(val name: String) : Value {
    abstract operator fun invoke(ctx: Context): Value
    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ForeignFunction
        return name == other.name
    }

    final override fun hashCode(): Int {
        return name.hashCode()
    }

    data class Context(
        val parameter: TableValue
    )
}


abstract class ForeignModule(val name: String) : Value {
    abstract fun functions(): Set<ForeignFunction>
}