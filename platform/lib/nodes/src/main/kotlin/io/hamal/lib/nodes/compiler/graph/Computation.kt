package io.hamal.lib.nodes.compiler.graph

import io.hamal.lib.nodes.NodeIndex
import io.hamal.lib.nodes.NodesGraph

internal data class ComputationGraph(val data: Map<NodeIndex, List<NodeIndex>>) {

    operator fun get(NodeIndex: NodeIndex): List<NodeIndex>? = data[NodeIndex]

    companion object {
        fun ComputationGraph(graph: NodesGraph): ComputationGraph {
            val computationGraph = mutableMapOf<NodeIndex, MutableList<NodeIndex>>()

            graph.connections.forEach { connection ->
                val outputId = connection.outputNode.index
                val inputId = connection.inputNode.index
                computationGraph.putIfAbsent(outputId, mutableListOf())
                computationGraph[outputId]!!.add(inputId)
            }
            return ComputationGraph(computationGraph)
        }

    }
}

internal fun breadthFirstSearch(graph: ComputationGraph, root: NodeIndex): List<NodeIndex> {
    val result = mutableListOf<NodeIndex>()

    if (graph[root] == null) {
        return listOf()
    }

    val toVisit = ArrayDeque<NodeIndex>().also { it.add(root) }

    while (toVisit.isNotEmpty()) {
        val vertex = toVisit.removeFirst()
        if (vertex !in result) {
            result.add(vertex)
            toVisit.addAll(graph[vertex]?.filterNot { it in result } ?: listOf())
        }
    }

    return result
}