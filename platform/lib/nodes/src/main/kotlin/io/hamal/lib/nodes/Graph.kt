package io.hamal.lib.nodes

import io.hamal.lib.nodes.node.Node

data class Graph(
    val nodes: List<Node>,
    val connections: List<Connection>
)