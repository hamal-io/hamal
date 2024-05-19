package io.hamal.lib.nodes.compiler.graph

import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.common.value.ValueType
import io.hamal.lib.nodes.*
import io.hamal.lib.nodes.ControlType.Companion.ControlType
import io.hamal.lib.nodes.compiler.graph.ComputationGraph.Companion.ComputationGraph
import io.hamal.lib.nodes.compiler.node.NodeCompiler
import io.hamal.lib.nodes.compiler.node.NodeCompilerRegistry

interface GraphCompiler {
    fun compile(graph: NodesGraph): ValueCode
}

class GraphCompilerImpl(private val registry: NodeCompilerRegistry) : GraphCompiler {

    override fun compile(graph: NodesGraph): ValueCode {
        val code = StringBuilder()

        val nodeCodeGenerators = mutableMapOf<NodeId, NodeCompiler>()
        val outputPortMapping = mutableMapOf<PortId, Pair<String, ValueType>>()

        val nodes = graph.nodes

        code.append("throw = require('std.throw').create()\n")

        for (node in nodes) {
            val controls = graph.controls.filter { it.nodeId == node.id }
            val inputTypes = controls.filterIsInstance<ControlCausesInvocation>().mapNotNull { control ->
                if (control is ControlWithPort) {
                    control.port?.type
                } else {
                    null
                }
            }

            val outputTypes = node.outputs.map { it.type }

            val generator = registry[node.type, inputTypes, outputTypes]

            val builder = StringBuilder()
            val args = List(generator.inputTypes.size) { "arg_${it + 1}" }.joinToString { it }


            builder.append("""function n_${node.id.stringValue}(${args})""")
            builder.append("\n")
            builder.append(generator.toCode(NodeCompiler.Context(node, controls.filter { it.nodeId == node.id })))
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


        val initNode = nodes.minByOrNull { it.id } ?: throw IllegalArgumentException("No Init node found")

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
                        val p1 = if (control is ControlInputString) {
                            val defaultValue = control.value.stringValue
                            "'${defaultValue}'"
                        } else {
                            outputPortMapping[connection.outputPort.id]!!.first
                        }

                        code.append("local p_1 = $p1 \n")

                        code.append("n_${inputNode.id}_1, n_${inputNode.id}_2 = n_${inputNode.id.stringValue}(p_1)")

                    } else if (controls.size == 2) {
                        var control = controls.first()
                        val p1 = if (control is ControlInputString) {
                            val defaultValue = control.value.stringValue
                            "'${defaultValue}'"
                        } else if (control is ControlInputBoolean) {
                            val defaultValue = control.value.booleanValue
                            "${defaultValue}"
                        } else {
                            outputPortMapping[connection.outputPort.id]!!.first
                        }

                        code.append("local p_1 = $p1 \n")

                        // FIXME is there a connection to the port? otherwise default to default value
                        control = controls.last()
                        require(control is ControlInputString)
                        val p2 = "'${control.value.stringValue}'"

                        code.append("local p_2 = $p2 \n")

                        code.append("n_${inputNode.id.stringValue}(p_1, p_2)")
                    } else {
                        TODO()
                    }

                } else {

                    if (controls.size == 1) {
                        val control = controls.first()

                        val p1 = if (control is ControlInputString) {
                            val defaultValue = control.value.stringValue
                            "'${defaultValue}'"
                        } else if (control is ControlInputBoolean) {
                            val defaultValue = control.value.booleanValue
                            "${defaultValue}"
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

                        if (control is ControlInputString) {
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
                        val p1 = if (control is ControlInputString) {
                            val defaultValue = control.value.stringValue
                            "${outputPortMapping[connection.outputPort.id]!!.first} or '${defaultValue}'"
                        } else if (control is ControlInputBoolean) {
                            val defaultValue = control.value.booleanValue
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

//        println(code.toString())

        return ValueCode(code.toString())
    }


}