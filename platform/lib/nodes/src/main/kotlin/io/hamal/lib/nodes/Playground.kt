package io.hamal.lib.nodes

import io.hamal.lib.typesystem.TypeNumber
import io.hamal.lib.typesystem.value.Value
import io.hamal.lib.typesystem.value.ValueNumber

data class ConstantNode(
    override val id: NodeId,
    override val outputPortIds: List<PortId>,
    val name: String,
    // type
    val value: Value
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


class Nodes {
    lateinit var rootNode: NodeId

    val nodes = mutableMapOf<NodeId, Node>()
    val connections = mutableMapOf<ConnectionId, Connection>()
    val ports = mutableMapOf<PortId, Port>()
}


fun main() {
    val nodes = Nodes()

    val variablePort = PortOutput(
        id = PortId(1),
        name = PortName("value"),
        type = TypeNumber
    )

    val variable = ConstantNode(
        id = NodeId(1),
        outputPortIds = listOf(variablePort.id),
        name = "v",
        value = ValueNumber(42)
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

    nodes.rootNode = variable.id
    nodes.nodes[variable.id] = variable
    nodes.nodes[filter.id] = filter
    nodes.nodes[print.id] = print

    nodes.ports[variablePort.id] = variablePort
    nodes.ports[filterInputPort.id] = filterInputPort
    nodes.ports[filterOutputPort.id] = filterOutputPort
    nodes.ports[printPort.id] = printPort

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

    nodes.connections[c1.id] = c1
    nodes.connections[c2.id] = c2

    println(nodes)
    runGraph(nodes)
}

fun runGraph(graph: Nodes) {
    println(graph)
}