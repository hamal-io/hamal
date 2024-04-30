package io.hamal.lib.value.type

import io.hamal.lib.common.domain.ValueObjectString
import io.hamal.lib.value.Property
import io.hamal.lib.value.ValueList

class TypeIdentifier(override val value: String) : ValueObjectString()

sealed class Type {
    abstract val identifier: TypeIdentifier

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Type
        return identifier == other.identifier
    }

    override fun hashCode(): Int {
        return identifier.hashCode()
    }
}

sealed class TypeList : Type() {
    abstract val valueType: Type
    operator fun invoke(vararg any: Any): ValueList {
        return ValueList(this, any.map { v -> Property.mapTypeToValue(valueType, v) })
    }
}





