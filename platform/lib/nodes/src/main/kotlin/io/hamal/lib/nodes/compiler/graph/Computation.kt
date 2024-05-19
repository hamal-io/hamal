package io.hamal.lib.nodes.compiler.graph

import io.hamal.lib.nodes.*

data class ComputationGraph(
    val dependencies: Map<NodeIndex, List<NodeIndex>>,
    val nodes: MutableMap<NodeIndex, Node>,
    val controls: MutableMap<ControlIndex, Control>,
    val connections: MutableMap<ConnectionIndex, Connection>
) {

    operator fun get(index: NodeIndex): List<NodeIndex>? = dependencies[index]

    companion object {
        fun ComputationGraph(graph: NodesGraph): ComputationGraph {
            val computationGraph = mutableMapOf<NodeIndex, MutableList<NodeIndex>>()

            graph.connections.forEach { connection ->
                val outputId = connection.outputNode.index
                val inputId = connection.inputNode.index
                computationGraph.putIfAbsent(outputId, mutableListOf())
                computationGraph[outputId]!!.add(inputId)
            }
            return ComputationGraph(
                dependencies = computationGraph,
                nodes = graph.nodes.associateBy { it.index }.toMutableMap(),
                controls = graph.controls.associateBy { it.index }.toMutableMap(),
                connections = graph.connections.associateBy { it.index }.toMutableMap()
            )
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