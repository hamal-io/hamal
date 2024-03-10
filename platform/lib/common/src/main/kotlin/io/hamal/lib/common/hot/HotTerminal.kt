package io.hamal.lib.common.hot

sealed interface HotTerminal<NODE_TYPE : HotTerminal<NODE_TYPE>> : HotNode<NODE_TYPE> {
    override val isTerminal get() : Boolean = true
    override fun asTerminal(): HotTerminal<*> = this
}