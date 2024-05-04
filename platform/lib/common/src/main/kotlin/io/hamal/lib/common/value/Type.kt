package io.hamal.lib.common.value


class TypeIdentifier(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun TypeIdentifier(value: String) = TypeIdentifier(ValueString(value))
    }
}

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

