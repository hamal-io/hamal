package io.hamal.lib.nodes.compiler.graph

import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.nodes.NodeType.Companion.NodeType
import io.hamal.lib.nodes.NodesGraph
import io.hamal.lib.nodes.compiler.graph.ComputationGraph.Companion.ComputationGraph
import io.hamal.lib.nodes.compiler.node.NodeCompilerRegistry

interface GraphCompiler {
    fun compile(graph: NodesGraph): ValueCode
}


class GraphCompilerImpl(registry: NodeCompilerRegistry) : GraphCompiler {

    override fun compile(graph: NodesGraph): ValueCode {
        val computationGraph = ComputationGraph(graph)
        val compiledNodes = nodeCompiler.compile(computationGraph).associateBy { it.index }

        val builder = StringBuilder()

        compiledNodes.values.forEach { compiledNode ->
            builder.append(compiledNode.code.stringValue)
            builder.append("\n")
        }

        val startNode = graph.nodes.find { it.type == NodeType("Start") }
            ?: throw IllegalArgumentException("No Init node found")

        builder.append("algo = require('std.algo').create()\n")
        builder.append("throw = require('std.throw').create()\n")
        builder.append("once = require('std.once').create()\n\n")

        builder.append("__F__ = {}\n")

        val orderedNodes = breadthFirstSearch(computationGraph, startNode.index)

        orderedNodes.forEach { inputNodeIndex ->
            val inputNode = graph.nodes.find { it.index == inputNodeIndex }!!
            builder.append("__F__[${inputNode.index}] = once(n_${inputNode.index}) \n")
        }

        builder.append("\n")
        builder.append("""local g = algo.graph.create()""")
        builder.append("\n")

        computationGraph.dependencies.forEach { (index, list) ->
            list.forEach { otherIndex ->
                builder.append("""algo.graph.add(g, ${index.longValue}, ${otherIndex.longValue})""")
                builder.append("\n")
            }
        }

        builder.append(
            """
            visited = algo.list.create()

            function prune_node(node)
                algo.list.add(visited, node)
            end

            function visit_node(cur)
                if algo.list.contains(visited, cur) == false then
                    algo.list.add(visited, cur)
                    __F__[cur]()

                    for _, edge in algo.graph.edges(g, cur) do
                        if edge.from == cur then
                            visit_node(edge.to)
                        end
                    end

                end

            end

            visit_node(1)

        """.trimIndent()
        )

        println(builder)

        return ValueCode(builder.toString())
    }

    private val nodeCompiler = NodeCompilerImpl(registry)

}