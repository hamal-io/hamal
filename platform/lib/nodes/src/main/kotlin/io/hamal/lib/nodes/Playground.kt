package io.hamal.lib.nodes

import io.hamal.lib.typesystem.TypeNumber


data class ConstantNode(
    override val id: NodeId,
    override val outputPortIds: List<PortId>,
    val name: String
) : NodeWithOutputs

class Condition

data class FilterNode(
    override val id: NodeId,
    override val inputPortIds: List<PortId>,
    override val outputPortIds: List<PortId>,
    val name: String,
    val conditions: List<Condition>
) : NodeWithInputs, NodeWithOutputs


data class PrintNode(
    override val id: NodeId,
    override val inputPortIds: List<PortId>
) : NodeWithInputs


class NodeGraph {
    lateinit var rootNode: NodeId

    val nodes = mutableMapOf<NodeId, Node>()
    val connections = mutableMapOf<ConnectionId, Connection>()
    val ports = mutableMapOf<PortId, Port>()
}


fun main() {
    val node = NodeGraph()

    val variablePort = PortOutput(
        id = PortId(1),
        name = PortName("value"),
        type = TypeNumber
    )

    val variable = ConstantNode(
        id = NodeId(1),
        outputPortIds = listOf(variablePort.id),
        name = "v"
    )

    val filterInputPort = PortInput(
        id = PortId(23),
        name = PortName("input"),
        type = TypeNumber
    )

    val filterOutputPort = PortOutput(
        id = PortId(24),
        name = PortName("output"),
        type = TypeNumber
    )

    val filter = FilterNode(
        id = NodeId(25),
        inputPortIds = listOf(filterInputPort.id),
        outputPortIds = listOf(filterOutputPort.id),
        name = "Test",
        conditions = listOf()
    )

    val printPort = PortInput(
        id = PortId(2),
        name = PortName("number"),
        type = TypeNumber
    )

    val print = PrintNode(
        id = NodeId(2),
        inputPortIds = listOf(printPort.id)
    )

    node.rootNode = variable.id
    node.nodes[variable.id] = variable
    node.nodes[filter.id] = filter
    node.nodes[print.id] = print

    node.ports[variablePort.id] = variablePort
    node.ports[filterInputPort.id] = filterInputPort
    node.ports[filterOutputPort.id] = filterOutputPort
    node.ports[printPort.id] = printPort

    val c1 = Connection(
        id = ConnectionId(1),
        outputNodeId = variable.id,
        outputSlotId = variablePort.id,
        inputNodeId = filter.id,
        inputSlotId = filterInputPort.id,
    )

    val c2 = Connection(
        id = ConnectionId(2),
        outputNodeId = filter.id,
        outputSlotId = filterOutputPort.id,
        inputNodeId = print.id,
        inputSlotId = printPort.id,
    )

    node.connections[c1.id] = c1
    node.connections[c2.id] = c2

    println(node)
    runGraph(node)
}

fun runGraph(graph: NodeGraph) {
    println(graph)
}