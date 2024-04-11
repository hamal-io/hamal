package io.hamal.lib.nodes

import io.hamal.lib.nodes.control.ControlConstant
import io.hamal.lib.nodes.control.ControlConstantString
import io.hamal.lib.nodes.control.ControlId
import io.hamal.lib.nodes.control.ControlInputString
import io.hamal.lib.nodes.node.Node
import io.hamal.lib.nodes.node.NodeId
import io.hamal.lib.nodes.node.NodeTitle
import io.hamal.lib.nodes.node.NodeType
import io.hamal.lib.typesystem.TypeNew
import io.hamal.lib.typesystem.TypeNumber
import io.hamal.lib.typesystem.TypeString
import io.hamal.lib.typesystem.value.ValueString


interface NodeCodeGenerator {
    val type: NodeType
    val inputTypes: List<TypeNew>
    val outputTypes: List<TypeNew>
    fun toCode(node: Node): String
}

object NodeCodeGeneratorConstant : NodeCodeGenerator {
    override val type: NodeType get() = NodeType("ConstantString")

    override val inputTypes: List<TypeNew> get() = listOf()
    override val outputTypes: List<TypeNew> get() = listOf(TypeString)

//    Notoverride val fields: List<Field>
//        get() = listOf(
//            Field(Field.Kind.String, "arg1")
//        )


    override fun toCode(node: Node): String {
        val controls = node.controls
        check(controls.size == 1)

        val control = controls[0]
        check(control is ControlConstant)

        if (control is ControlConstantString) {
            return """
            return  '${control.value.stringValue}'
        """.trimIndent()
        } else {
            TODO()
        }
    }
}


interface NodeCodeGeneratorPrint : NodeCodeGenerator {
    override val type: NodeType get() = NodeType("Print")

    object Number : NodeCodeGeneratorPrint {
        override val inputTypes: List<TypeNew> get() = listOf(TypeNumber)
        override val outputTypes: List<TypeNew> get() = listOf()


        override fun toCode(node: Node): kotlin.String {
            return """
            print(arg_1)
            return
        """.trimIndent()
        }

    }

    object String : NodeCodeGeneratorPrint {
        override val inputTypes: List<TypeNew> get() = listOf(TypeString)
        override val outputTypes: List<TypeNew> get() = listOf()

        override fun toCode(node: Node): kotlin.String {
            return """
            print(arg_1)
            return
        """.trimIndent()
        }

    }
}


fun main() {

    val generators = listOf(
        NodeCodeGeneratorConstant,
        NodeCodeGeneratorPrint.String
    )

    val nodes = listOf(
        Node(
            id = NodeId(1),
            type = NodeType("ConstantString"),
            title = NodeTitle("Some Title"),
            position = Position(0, 0),
            size = Size(200, 200),
            controls = listOf(ControlConstantString(ControlId(23), ValueString("Hamal Rocks")),),
            outputs = listOf(PortOutput(PortId(222), TypeString))
        ),
        Node(
            id = NodeId(2),
            type = NodeType("Print"),
            title = NodeTitle("Some Title"),
            position = Position(0, 0),
            size = Size(200, 200),
            controls = listOf(
                ControlInputString(
                    ControlId(24),
                    PortInput(PortId(333), TypeString),
                    defaultValue = ValueString("default string")
                )
            ),
            outputs = listOf()
        ),
    )

    val connections = listOf(
        Connection(
            id = ConnectionId(100),
            outputNode = Connection.Node(NodeId(1)),
            outputPort = Connection.Port(id = PortId(222)),
            inputNode = Connection.Node(NodeId(2)),
            inputPort = Connection.Port(id = PortId(333))
        )
    )

    val graph = Graph(nodes, connections)
    println(graph)

    val code = StringBuilder()

    val nodeCodeGenerators = mutableMapOf<NodeId, NodeCodeGenerator>()
    val outputPortMapping = mutableMapOf<PortId, Pair<String, TypeNew>>()

    for (node in nodes) {
        val generator = generators.find { it.type == node.type }!!

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


    val nodesInvoked = mutableMapOf<NodeId, NodeCodeGenerator>()


    // FIXME breath first
    for (connection in connections) {
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
        code.append("n_${inputNode.id.value.value.toString(16)}(${outputPortMapping[connection.outputPort.id]!!.first})")
    }


    println(code.toString())

}