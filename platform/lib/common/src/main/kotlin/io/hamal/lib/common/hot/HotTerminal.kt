package io.hamal.lib.common.hot

sealed interface HotTerminal : HotNode {
    override val isTerminal get() : Boolean = true
    override fun asTerminal(): HotTerminal = this

    override fun deepCopy(): HotNode = this
}