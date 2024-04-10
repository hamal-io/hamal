package io.hamal.lib.nodes

import io.hamal.lib.nodes.control.*
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
    fun toCode(control: Control): String
}

object NodeCodeGeneratorConstant : NodeCodeGenerator {
    override val type: NodeType get() = NodeType("ConstantString")

    override val inputTypes: List<TypeNew> get() = listOf()
    override val outputTypes: List<TypeNew> get() = listOf(TypeString)

//    Notoverride val fields: List<Field>
//        get() = listOf(
//            Field(Field.Kind.String, "arg1")
//        )


    override fun toCode(control: Control): String {
        return """
            return  'hello hamal'
        """.trimIndent()
    }
}


interface NodeCodeGeneratorPrint : NodeCodeGenerator {
    override val type: NodeType get() = NodeType("Print")

    object Number : NodeCodeGeneratorPrint {
        override val inputTypes: List<TypeNew> get() = listOf(TypeNumber)
        override val outputTypes: List<TypeNew> get() = listOf()


        override fun toCode(control: Control): kotlin.String {
            return """
            print(arg_1)
            return
        """.trimIndent()
        }

    }

    object String : NodeCodeGeneratorPrint {
        override val inputTypes: List<TypeNew> get() = listOf(TypeString)
        override val outputTypes: List<TypeNew> get() = listOf()

        override fun toCode(control: Control): kotlin.String {
            return """
            print(arg_1)
            return
        """.trimIndent()
        }

    }
}


fun main() {
    val controls = listOf(
        ControlConstantString(ControlId(1), ValueString("Hamal Rocks")),
        ControlNone
    )

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
            controls = listOf(
                ControlConstantString(ControlId(23), ValueString("Hamal Rocks")),
            ),
            outputs = listOf(
                PortOutput(PortId(222), TypeString)
            )
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

//    val builder = StringBuilder()
//
//    generators.forEachIndexed { index, generator ->
//        val nodeId = index + 1
//
//        val args = List(generator.inputTypes.size) { "arg_${it + 1}" }.joinToString { it }
//
//        builder.append("""function n_${nodeId}(${args})""")
//        builder.append("\n")
//        builder.append(generator.toCode(controls[index]))
//        builder.append("\n")
//        builder.append("""end""")
//        builder.append("\n")
//        builder.append("\n")
//    }
//
//    println(builder.toString())
//
//    builder.append("\n")
//    builder.append("\n")
//
//    generators.forEachIndexed { index, generator ->
//        val nodeId = index + 1
//        val outputs = List(generator.outputTypes.size) { "node_${nodeId}_${it + 1}" }.joinToString { it }
//        builder.append(outputs)
//
//        if (generator.outputTypes.size > 0) {
//            builder.append(" = ")
//            builder.append("n_${nodeId}()")
//        } else {
//            builder.append("n_${nodeId}(node_1_1)")
//        }
//
//        builder.append("\n")
//    }
//
//    println(builder.toString())


}