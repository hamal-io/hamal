package io.hamal.lib.nodes.compiler.node

import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.nodes.*
import io.hamal.lib.nodes.compiler.graph.ComputationGraph


abstract class AbstractNode {
    abstract val type: NodeType
    abstract val version: NodeVersion

    abstract fun toCode(ctx: Context): ValueCode

    // FIXME optimize this - just for quick testing
    data class Context(
        val graph: ComputationGraph,
        val node: Node
    ) {

        fun nodeOfPort(portIndex: PortIndex): Node {

            return graph.nodes.values.mapNotNull { node ->
                if (portsOfNode(node.index).map { it.index }.contains(portIndex)) {
                    node
                } else {
                    null
                }
            }.first()
        }

        fun portsOfNode(nodeIndex: NodeIndex) = graph.connections.values.mapNotNull { connection ->
            if (connection.inputNode.index == nodeIndex) {
                connection.inputPort
            } else if (connection.outputNode.index == nodeIndex) {
                connection.outputPort
            } else {
                null
            }
        }

        fun controlsOfNode(nodeIndex: NodeIndex) = graph.controls.values.filter { it.nodeIndex == node.index }

        fun getConnection(portIndex: PortIndex) = graph.connections.values.firstOrNull {
            it.inputPort.index == portIndex || it.outputPort.index == portIndex
        }!!
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as AbstractNode
        return type == other.type
    }

    override fun hashCode(): Int {
        return type.hashCode()
    }

}
