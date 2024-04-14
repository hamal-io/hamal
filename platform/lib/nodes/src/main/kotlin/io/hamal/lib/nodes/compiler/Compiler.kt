package io.hamal.lib.nodes.compiler

import io.hamal.lib.nodes.NodeId
import io.hamal.lib.nodes.NodeType
import io.hamal.lib.nodes.NodesGraph
import io.hamal.lib.nodes.PortId
import io.hamal.lib.nodes.compiler.ComputationGraph.Companion.ComputationGraph
import io.hamal.lib.nodes.control.ControlInput
import io.hamal.lib.nodes.control.ControlInputString
import io.hamal.lib.nodes.control.ControlType
import io.hamal.lib.nodes.generator.Generator
import io.hamal.lib.nodes.generator.GeneratorRegistry
import io.hamal.lib.typesystem.TypeNew

class Compiler(
    private val generatorRegistry: GeneratorRegistry
) {

    fun compile(graph: NodesGraph): String {
        val code = StringBuilder()

        val nodeCodeGenerators = mutableMapOf<NodeId, Generator>()
        val outputPortMapping = mutableMapOf<PortId, Pair<String, TypeNew>>()

        val nodes = graph.nodes

        for (node in nodes) {

            val inputTypes = node.controls.mapNotNull { control ->
                if (control is ControlInput) {
                    control.port.inputType
                } else {
                    null
                }
            }

            val outputTypes = node.outputs.map { it.outputType }

            val generator = generatorRegistry[node.type, inputTypes, outputTypes]

            val builder = StringBuilder()
            val args = List(generator.inputTypes.size) { "arg_${it + 1}" }.joinToString { it }

            builder.append("""function n_${node.id.value.value.toString(16)}(${args})""")
            builder.append("\n")
            builder.append(generator.toCode(node))
            builder.append("\n")
            builder.append("""end""")
            builder.append("\n")
            builder.append("\n")

            nodeCodeGenerators[node.id] = generator

            code.append(builder.toString())

            node.outputs.mapIndexed { index, portOutput ->
                outputPortMapping[portOutput.id] = "n_${node.id.value.value.toString(16)}_${index + 1}" to generator.outputTypes[index]
            }
        }

        code.append("\n")
        code.append("\n")

        val initNode = nodes.find { it.type == NodeType("INIT") } ?: throw IllegalArgumentException("No INIT node found")
        val computationGraph = ComputationGraph(graph)
        val orderedNodeIds = breadthFirstSearch(computationGraph, initNode.id)

        orderedNodeIds.forEach { inputNodeId ->
            val inputNode = nodes.find { it.id == inputNodeId }!!

            val connections = graph.connections.filter { it.inputNode.id == inputNodeId }
            if (connections.isEmpty()) {
                code.append("n_${inputNode.id.value.value.toString(16)}_1 = n_1()\n")
            } else {

                val connection = connections.first()

                if (inputNode.controls.any { it.type == ControlType.Invoke }) {
                    val controls = inputNode.controls.filterNot { it.type == ControlType.Invoke }

                    if (controls.size == 1) {
                        var control = controls.first()
                        val p1 = if (control is ControlInputString) {
                            val defaultValue = control.defaultValue.stringValue
                            "'${defaultValue}'"
                        } else {
                            outputPortMapping[connection.outputPort.id]!!.first
                        }

                        code.append("local p_1 = $p1 \n")
                        code.append("n_${inputNode.id.value.value.toString(16)}(p_1)")

                    } else if (controls.size == 2) {
                        var control = controls.first()
                        val p1 = if (control is ControlInputString) {
                            val defaultValue = control.defaultValue.stringValue
                            "'${defaultValue}'"
                        } else {
                            outputPortMapping[connection.outputPort.id]!!.first
                        }

                        code.append("local p_1 = $p1 \n")

                        // FIXME is there a connection to the port? otherwise default to default value
                        control = controls.last()
                        require(control is ControlInputString)
                        val p2 = "'${control.defaultValue.stringValue}'"

                        code.append("local p_2 = $p2 \n")

                        code.append("n_${inputNode.id.value.value.toString(16)}(p_1, p_2)")
                    } else {
                        TODO()
                    }

                } else {
                    val controls = inputNode.controls

                    if (controls.size == 1) {
                        val control = controls.first()
                        if (control is ControlInputString) {
                            val defaultValue = control.defaultValue.stringValue
                            code.append("n_${inputNode.id.value.value.toString(16)}(${outputPortMapping[connection.outputPort.id]!!.first} or '${defaultValue}')")
                        } else {
                            code.append("n_${inputNode.id.value.value.toString(16)}(${outputPortMapping[connection.outputPort.id]!!.first})")
                        }
                    } else if (controls.size == 2) {

                        var control = controls.first()
                        val p1 = if (control is ControlInputString) {
                            val defaultValue = control.defaultValue.stringValue
                            "${outputPortMapping[connection.outputPort.id]!!.first} or '${defaultValue}'"
                        } else {
                            outputPortMapping[connection.outputPort.id]!!.first
                        }

                        code.append("local p_1 = $p1 \n")

                        // FIXME is there a connection to the port? otherwise default to default value
                        control = controls.last()
                        require(control is ControlInputString)
                        val p2 = connections.find { it.inputPort.id == control.port.id }?.let { connection ->
                            // FIXME resolve variable name
                            null
                        } ?: "'${control.defaultValue.stringValue}'"

                        code.append("local p_2 = $p2 \n")

                        code.append("n_${inputNode.id.value.value.toString(16)}(p_1, p_2)")
                    } else {
                        code.append("n_${inputNode.id.value.value.toString(16)}(${outputPortMapping[connection.outputPort.id]!!.first})")
                    }
                }
            }
        }

//        println(code)

        return code.toString()
    }


}