package io.hamal.lib.nodes.compiler.graph

import io.hamal.lib.nodes.NodeId
import io.hamal.lib.nodes.NodesGraph

internal data class ComputationGraph(val data: Map<NodeId, List<NodeId>>) {

    operator fun get(nodeId: NodeId): List<NodeId>? = data[nodeId]

    companion object {
        fun ComputationGraph(graph: NodesGraph): ComputationGraph {
            val computationGraph = mutableMapOf<NodeId, MutableList<NodeId>>()

            graph.connections.forEach { connection ->
                val outputId = connection.outputNode.id
                val inputId = connection.inputNode.id
                computationGraph.putIfAbsent(outputId, mutableListOf())
                computationGraph[outputId]!!.add(inputId)
            }
            return ComputationGraph(computationGraph)
        }

    }
}

internal fun breadthFirstSearch(graph: ComputationGraph, root: NodeId): List<NodeId> {
    val result = mutableListOf<NodeId>()

    if (graph[root] == null) {
        return listOf()
    }

    val toVisit = ArrayDeque<NodeId>().also { it.add(root) }

    while (toVisit.isNotEmpty()) {
        val vertex = toVisit.removeFirst()
        if (vertex !in result) {
            result.add(vertex)
            toVisit.addAll(graph[vertex]?.filterNot { it in result } ?: listOf())
        }
    }

    return result
}