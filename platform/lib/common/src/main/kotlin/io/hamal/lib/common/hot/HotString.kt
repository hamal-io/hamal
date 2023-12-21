package io.hamal.lib.common.hot

class HotString(val value: String) : HotTerminal {

    override val isString get() : Boolean = true

    override fun asString(): HotString = this

    override val stringValue get() : String = value

    override fun toString(): String = stringValue

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as HotString
        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}