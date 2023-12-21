package io.hamal.lib.common.hot

class HotBoolean(val value: Boolean) : HotTerminal {
    override val isBoolean get() : Boolean = true

    override fun asBoolean(): HotBoolean = this

    override val booleanValue get() :Boolean = super.booleanValue

    override fun asString(): String = toString()

    override fun toString(): String = value.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as HotBoolean
        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}
