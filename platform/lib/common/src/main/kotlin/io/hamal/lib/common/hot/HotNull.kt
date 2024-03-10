package io.hamal.lib.common.hot

data object HotNull : HotTerminal<HotNull> {
    override val isNull get(): Boolean = true
    override fun asNull(): HotNull = this
    override fun deepCopy() = this
}