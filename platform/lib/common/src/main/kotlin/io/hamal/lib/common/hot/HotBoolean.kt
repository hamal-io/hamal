package io.hamal.lib.common.hot

@JvmInline
value class HotBoolean(val value: Boolean) : HotTerminal<HotBoolean> {
    override val isBoolean get() : Boolean = true

    override fun asBoolean(): HotBoolean = this

    override fun asString(): HotString = HotString(stringValue)

    override val booleanValue get() : Boolean = value

    override val stringValue get() : String = value.toString()

    override fun deepCopy() = this

    override fun toString(): String = value.toString()
}
