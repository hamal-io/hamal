package io.hamal.lib.nodes

import io.hamal.lib.nodes.control.ControlInput
import io.hamal.lib.nodes.control.ControlInputString
import io.hamal.lib.nodes.generator.Generator
import io.hamal.lib.nodes.generator.GeneratorRegistry
import io.hamal.lib.typesystem.TypeNew

class Compiler(
    private val generatorRegistry: GeneratorRegistry
) {

    fun compile(graph: Graph): String {
        val code = StringBuilder()

        val nodeCodeGenerators = mutableMapOf<NodeId, Generator>()
        val outputPortMapping = mutableMapOf<PortId, Pair<String, TypeNew>>()

        val nodes = graph.nodes
        val connections = graph.connections

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

//        println(builder)
            nodeCodeGenerators[node.id] = generator

            code.append(builder.toString())

            node.outputs.mapIndexed { index, portOutput ->
                outputPortMapping[portOutput.id] = "node_1_1" to generator.outputTypes[index]
            }
        }

        code.append("\n")
        code.append("\n")


//        if (connections.size == 0) {
//            if (nodes.size == 1) {
//                code.append("n_${nodes.first().id.value.value.toString(16)}()")
//            } else {
//                TODO()
//            }
//        }

        // FIXME find Init node and go from there

        // FIXME breath first
//        for (connection in connections) {
        val connection = connections[0]
        val outputNode = nodes.find { it.id == connection.outputNode.id }!!
        val outputGenerator = nodeCodeGenerators[connection.outputNode.id]!!

        val inputNode = nodes.find { it.id == connection.inputNode.id }!!

        //        val nodeId = index + 1
        val outputs = List(outputGenerator.outputTypes.size) { "node_${outputNode.id.value.value.toString(16)}_${it + 1}" }.joinToString { it }
        code.append(outputs)
//
//        if (outputGenerator.outputTypes.size > 0) {
        code.append(" = ")
        code.append("n_${outputNode.id.value.value.toString(16)}()")
//        }
//

        code.append("\n")

        // assumes the all dependency were already called - maybe a function can be called lazy be default, which probably simplifies the implementation
        if (inputNode.controls.size == 1) {
            val control = inputNode.controls.first()
            if (control is ControlInputString) {
                val defaultValue = control.defaultValue.stringValue
                code.append("n_${inputNode.id.value.value.toString(16)}(${outputPortMapping[connection.outputPort.id]!!.first} or '${defaultValue}')")
            } else {
                code.append("n_${inputNode.id.value.value.toString(16)}(${outputPortMapping[connection.outputPort.id]!!.first})")
            }
        } else if (inputNode.controls.size == 2) {

            var control = inputNode.controls.first()
            val p1 = if (control is ControlInputString) {
                val defaultValue = control.defaultValue.stringValue
                "${outputPortMapping[connection.outputPort.id]!!.first} or '${defaultValue}'"
            } else {
                "${outputPortMapping[connection.outputPort.id]!!.first})"
            }

            code.append("local p_1 = $p1 \n")

            // FIXME is there a connection to the port? otherwise default to default value
            control = inputNode.controls.last()
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
//        }


        println(code.toString())

        return code.toString()
    }

}