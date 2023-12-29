package io.hamal.lib.common.hot

object HotNull : HotTerminal {
    override val isNull get(): Boolean = true
    override fun asNull(): HotNull = this
}