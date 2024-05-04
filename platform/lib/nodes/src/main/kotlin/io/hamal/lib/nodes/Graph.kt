package io.hamal.lib.nodes


data class NodesGraph(
    val nodes: List<Node>,
    val connections: List<Connection>,
    val controls: List<Control>
)