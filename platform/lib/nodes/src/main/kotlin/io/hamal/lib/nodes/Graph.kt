package io.hamal.lib.nodes

import io.hamal.lib.nodes.control.Control

data class NodesGraph(
    val nodes: List<Node>,
    val connections: List<Connection>,
    val controls: List<Control>
)