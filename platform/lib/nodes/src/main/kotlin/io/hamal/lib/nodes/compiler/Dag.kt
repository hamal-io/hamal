package io.hamal.lib.nodes.compiler

import io.hamal.lib.nodes.node.NodeId

data class DagNode(
    val id: NodeId,
    val code: String
)

class Dag(val nodes: List<DagNode>)

