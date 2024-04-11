package io.hamal.lib.nodes

import io.hamal.lib.nodes.control.ControlConstantString
import io.hamal.lib.nodes.control.ControlInputString
import io.hamal.lib.typesystem.TypeString
import io.hamal.lib.typesystem.value.ValueString


fun main() {

    val nodes = listOf(
        Node(
            id = NodeId(1),
            type = NodeType("ConstantString"),
            title = NodeTitle("Some Title"),
            position = Position(0, 0),
            size = Size(200, 200),
            controls = listOf(ControlConstantString(ValueString("Hamal Rocks"))),
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

    val code = Compiler.compile(graph)
    println(code)

}