package io.hamal.lib.nodes.compiler

import io.hamal.lib.common.value.Type
import io.hamal.lib.nodes.ControlTextArea
import io.hamal.lib.nodes.NodeId
import io.hamal.lib.nodes.NodeType.Companion.NodeType
import io.hamal.lib.nodes.NodesGraph
import io.hamal.lib.nodes.PortId
import io.hamal.lib.nodes.compiler.ComputationGraph.Companion.ComputationGraph
import io.hamal.lib.nodes.control.ControlInput
import io.hamal.lib.nodes.control.ControlInvoke
import io.hamal.lib.nodes.control.ControlType.Companion.ControlType
import io.hamal.lib.nodes.generator.Generator
import io.hamal.lib.nodes.generator.GeneratorRegistry

class Compiler(
    private val generatorRegistry: GeneratorRegistry
) {

    fun compile(graph: NodesGraph): String {
        val code = StringBuilder()

        val nodeCodeGenerators = mutableMapOf<NodeId, Generator>()
        val outputPortMapping = mutableMapOf<PortId, Pair<String, Type>>()

        val nodes = graph.nodes

        for (node in nodes) {
            val controls = graph.controls.filter { it.nodeId == node.id }
            val inputTypes = controls.filter { it !is ControlInvoke }.mapNotNull { control ->
                if (control is ControlInput) {
                    control.port.type
                } else {
                    null
                }
            }

            val outputTypes = node.outputs.map { it.type }

            val generator = generatorRegistry[node.type, inputTypes, outputTypes]

            val builder = StringBuilder()
            val args = List(generator.inputTypes.size) { "arg_${it + 1}" }.joinToString { it }

            builder.append("""function n_${node.id.stringValue}(${args})""")
            builder.append("\n")
            builder.append(generator.toCode(node, controls.filter {
                it.nodeId == node.id


            }))
            builder.append("\n")
            builder.append("""end""")
            builder.append("\n")
            builder.append("\n")

            nodeCodeGenerators[node.id] = generator

            code.append(builder.toString())

            node.outputs.mapIndexed { index, portOutput ->
                outputPortMapping[portOutput.id] =
                    "n_${node.id.stringValue}_${index + 1}" to generator.outputTypes[index]
            }
        }

        code.append("\n")
        code.append("\n")

        val initNode =
            nodes.find { it.type == NodeType("Init") } ?: throw IllegalArgumentException("No INIT node found")
        val computationGraph = ComputationGraph(graph)
        val orderedNodeIds = breadthFirstSearch(computationGraph, initNode.id)

        orderedNodeIds.forEach { inputNodeId ->
            val inputNode = nodes.find { it.id == inputNodeId }!!

            val connections = graph.connections.filter { it.inputNode.id == inputNodeId }
            if (connections.isEmpty()) {
                code.append("n_${inputNode.id.stringValue}_1 = n_1()\n")
            } else {

                val connection = connections.first()
                val controls = graph.controls.filter { it.nodeId == inputNodeId }
                if (controls.any { it.type == ControlType("Invoke") }) {
                    val controls = controls.filterNot { it.type == ControlType("Invoke") }

                    if (controls.isEmpty()) {


                        val p1 = outputPortMapping[connection.outputPort.id]!!.first


                        code.append("local p_1 = $p1 \n")

                        code.append("if p_1 ~= nil then \n")

                        code.append("n_${inputNode.id.stringValue}(p_1)\n")

                        code.append("end\n")


                    } else if (controls.size == 1) {


                        var control = controls.first()
                        val p1 = if (control is ControlTextArea) {
                            val defaultValue = control.value.stringValue
                            "'${defaultValue}'"
                        } else {
                            outputPortMapping[connection.outputPort.id]!!.first
                        }

                        code.append("local p_1 = $p1 \n")
                        code.append("n_${inputNode.id.stringValue}(p_1)")

                    } else if (controls.size == 2) {
                        var control = controls.first()
                        val p1 = if (control is ControlTextArea) {
                            val defaultValue = control.value.stringValue
                            "'${defaultValue}'"
                        } else {
                            outputPortMapping[connection.outputPort.id]!!.first
                        }

                        code.append("local p_1 = $p1 \n")

                        // FIXME is there a connection to the port? otherwise default to default value
                        control = controls.last()
                        require(control is ControlTextArea)
                        val p2 = "'${control.value.stringValue}'"

                        code.append("local p_2 = $p2 \n")

                        code.append("n_${inputNode.id.stringValue}(p_1, p_2)")
                    } else {
                        TODO()
                    }

                } else {

                    if (controls.size == 1) {
                        val control = controls.first()

                        val p1 = if (control is ControlTextArea) {
                            val defaultValue = control.value.stringValue
                            "'${defaultValue}'"
                        } else {
                            outputPortMapping[connection.outputPort.id]!!.first
                        }

                        code.append("local p_1 = $p1 \n")

                        code.append("if p_1 ~= nil then \n")

                        val fnResult = inputNode.outputs.map { portOutput -> outputPortMapping[portOutput.id]!!.first }
                            .joinToString(", ")
                        if (inputNode.outputs.size > 0) {
                            code.append(fnResult)
                            code.append(" = ")
                        }

                        if (control is ControlTextArea) {
                            val defaultValue = control.value.stringValue
                            code.append("n_${inputNode.id.stringValue}(${outputPortMapping[connection.outputPort.id]!!.first} or '${defaultValue}')")
                        } else {
                            code.append("n_${inputNode.id.stringValue}(${outputPortMapping[connection.outputPort.id]!!.first})")
                        }

                        code.append("\nelse\n")

                        inputNode.outputs.forEach { portOutput ->
                            code.append(outputPortMapping[portOutput.id]!!.first)
                            code.append(" = nil\n")
                        }

                        code.append("end\n")

                    } else if (controls.size == 2) {

                        var control = controls.first()
                        val p1 = if (control is ControlTextArea) {
                            val defaultValue = control.value.stringValue
                            "${outputPortMapping[connection.outputPort.id]!!.first} or '${defaultValue}'"
                        } else {
                            outputPortMapping[connection.outputPort.id]!!.first
                        }

                        code.append("local p_1 = $p1 \n")

                        // FIXME is there a connection to the port? otherwise default to default value
                        control = controls.last()
                        require(control is ControlTextArea)
                        val p2 = connections.find { it.inputPort.id == control.port.id }?.let { connection ->
                            // FIXME resolve variable name
                            null
                        } ?: "'${control.value.stringValue}'"

                        code.append("local p_2 = $p2 \n")

                        code.append("n_${inputNode.id.stringValue}(p_1, p_2)")
                    } else {


                        code.append("n_${inputNode.id.stringValue}(${outputPortMapping[connection.outputPort.id]!!.first})")
                    }
                }
                code.append("\n")
            }
        }

//        println(code)

        return code.toString()
    }


}