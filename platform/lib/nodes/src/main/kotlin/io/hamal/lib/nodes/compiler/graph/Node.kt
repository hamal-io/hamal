package io.hamal.lib.nodes.compiler.graph

import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.nodes.Form
import io.hamal.lib.nodes.NodeIndex
import io.hamal.lib.nodes.compiler.node.AbstractNode.Context
import io.hamal.lib.nodes.compiler.node.NodeCompilerRegistry

internal data class CompiledNode(
    val index: NodeIndex,
    val code: ValueCode,
    val outputs: List<Form>
)

internal class NodeCompilerImpl(
    private val registry: NodeCompilerRegistry
) {

    fun compile(graph: ComputationGraph): List<CompiledNode> {
        return graph.nodes.values.map { node ->
            val ctx = Context(graph, node)

            val builder = StringBuilder()

            val controls = graph.controls.values.filter { it.nodeIndex == node.index }

            val generator = registry[node.type, node.version]

            builder.append("""function n_${node.index}()""")
            builder.append("\n")
            builder.append(generator.toCode(ctx))
            builder.append("\n")
            builder.append("""end""")
            builder.append("\n")

            CompiledNode(
                index = node.index,
                code = ValueCode(builder.toString()),
                outputs = node.outputs.map { it.form }
            )
        }
    }
}
