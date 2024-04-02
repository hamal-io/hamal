package io.hamal.lib.nodes

data class ConstantNode(
    override val id: NodeId,
    override val outputSlotIds: List<SlotId>,
    val name: String,
    // type
    val value: Int
) : NodeWithOutputs


data class PrintNode(
    override val id: NodeId,
    override val inputSlotIds: List<SlotId>
) : NodeWithInputs


class Nodes {

    lateinit var rootNode: NodeId

    val nodes = mutableMapOf<NodeId, Node>()
    val connections = mutableMapOf<ConnectionId, Connection>()
    val ports = mutableMapOf<SlotId, Slot>()

}

fun main() {
    val nodes = Nodes()

    val variableSlot = SlotOutput(
        id = SlotId(1),
        name = SlotName("value")
    )

    val variable = ConstantNode(
        id = NodeId(1),
        outputSlotIds = listOf(variableSlot.id),
        name = "v",
        value = 43
    )

    val printSlot = SlotInput(
        id = SlotId(2),
        name = SlotName("message")
    )

    val print = PrintNode(
        id = NodeId(2),
        inputSlotIds = listOf(printSlot.id)
    )

    nodes.rootNode = variable.id
    nodes.nodes[variable.id] = variable
    nodes.nodes[print.id] = print

    nodes.ports[variableSlot.id] = variableSlot
    nodes.ports[printSlot.id] = printSlot

    val connection = Connection(
        id = ConnectionId(1),
        outputNodeId = variable.id,
        outputSlotId = variableSlot.id,
        inputNodeId = print.id,
        inputSlotId = printSlot.id,
    )

    nodes.connections[connection.id] = connection
    println(nodes)
    runGraph(nodes)
}

fun runGraph(graph: Nodes) {
    println(graph)
}