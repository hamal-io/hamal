package io.hamal.lib.nodes.compiler.graph

import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.nodes.Form
import io.hamal.lib.nodes.NodesGraph
import io.hamal.lib.nodes.compiler.node.AbstractNode
import io.hamal.lib.nodes.compiler.node.NodeCompilerRegistry

internal data class CompiledNode(
    val code: ValueCode,
    val inputs: List<Form>,
    val outputs: List<Form>
)

internal class NodeCompilerImpl(
    private val registry: NodeCompilerRegistry
) {

    fun compile(graph: NodesGraph): List<CompiledNode> {
        return graph.nodes.map { node ->
            val builder = StringBuilder()

            val controls = graph.controls.filter { it.nodeIndex == node.index }

            val inputs = node.inputs.map { port -> port.type }

            val outputs = node.outputs.map { it.type }

            val generator = registry[node.type, inputs, outputs]

            val args = List(generator.inputs.size) { "arg_${it + 1}" }.joinToString { it }

            builder.append("""function n_${node.index.longValue}(${args})""")
            builder.append("\n")
            builder.append(generator.toCode(AbstractNode.Context(node, controls.filter { it.nodeIndex == node.index })))
            builder.append("\n")
            builder.append("""end""")
            builder.append("\n")

            CompiledNode(
                code = ValueCode(builder.toString()),
                inputs = inputs,
                outputs = List(node.outputs.size) { index -> generator.outputs[index] }
            )
        }
    }
}
