package io.hamal.lib.common.hot

@JvmInline
value class HotString(val value: String) : HotTerminal<HotString> {

    override val isString get() : Boolean = true

    override fun asString(): HotString = this

    override val stringValue get() : String = value

    override fun deepCopy() = this

    override fun toString(): String = stringValue
}