package io.hamal.lib.nodes

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.snowflake.PartitionSourceImpl
import io.hamal.lib.common.snowflake.SnowflakeGenerator
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.typesystem.TypeNumber


//data class ConstantNode(
//    override val id: NodeId,
//    override val outputPortIds: List<PortId>,
//    val name: String
//) : NodeWithOutputs
//
//class Condition
//
//data class FilterNode(
//    override val id: NodeId,
//    override val inputPortIds: List<PortId>,
//    override val outputPortIds: List<PortId>,
//    val name: String,
//    val conditions: List<Condition>
//) : NodeWithInputs, NodeWithOutputs
//
//
//data class PrintNode(
//    override val id: NodeId,
//    override val inputPortIds: List<PortId>
//) : NodeWithInputs
//

class Nodes {
    lateinit var rootNode: NodeId

    val nodes = mutableListOf<Node>()
    val connections = mutableListOf<Connection>()
    val ports = mutableListOf<Port>()
}

interface GenerateDomainId {
    operator fun <ID : ValueObjectId> invoke(ctor: (SnowflakeId) -> ID): ID
}

object IdGeneratorImpl : GenerateDomainId {
    override fun <ID : ValueObjectId> invoke(ctor: (SnowflakeId) -> ID): ID {
        return ctor(generator.next())
    }
}

private val generator = SnowflakeGenerator(PartitionSourceImpl(1))

fun main() {
    val generateDomainId = IdGeneratorImpl

    val nodes = Nodes()

    val variablePort = Port(
        id = generateDomainId(::PortId),
        name = PortName("value"),
        type = Port.Type.Output,
        valueType = TypeNumber
    )

    val variable = Node(
        id = generateDomainId(::NodeId),
        type = NodeType("LOAD_CONSTANT"),
        inputPortIds = listOf(),
        outputPortIds = listOf(variablePort.id),
    )

    val filterInputPort = Port(
        id = generateDomainId(::PortId),
        name = PortName("input"),
        type = Port.Type.Input,
        valueType = TypeNumber
    )

    val filterOutputPort = Port(
        id = generateDomainId(::PortId),
        name = PortName("output"),
        type = Port.Type.Output,
        valueType = TypeNumber
    )

    val filter = Node(
        id = generateDomainId(::NodeId),
        type = NodeType("FILTER_OBJECT"),
        inputPortIds = listOf(filterInputPort.id),
        outputPortIds = listOf(filterOutputPort.id),
    )

    val printPort = Port(
        id = generateDomainId(::PortId),
        name = PortName("number"),
        type = Port.Type.Output,
        valueType = TypeNumber
    )

    val print = Node(
        id = generateDomainId(::NodeId),
        type = NodeType("PRINT"),
        inputPortIds = listOf(printPort.id),
        outputPortIds = listOf()
    )

    nodes.rootNode = variable.id
    nodes.nodes.add(variable)
    nodes.nodes.add(filter)
    nodes.nodes.add(print)

    nodes.ports.add(variablePort)
    nodes.ports.add(filterInputPort)
    nodes.ports.add(filterOutputPort)
    nodes.ports.add(printPort)

    val c1 = Connection(
        id = generateDomainId(::ConnectionId),
        outputNodeId = variable.id,
        outputSlotId = variablePort.id,
        inputNodeId = filter.id,
        inputSlotId = filterInputPort.id,
    )

    val c2 = Connection(
        id = generateDomainId(::ConnectionId),
        outputNodeId = filter.id,
        outputSlotId = filterOutputPort.id,
        inputNodeId = print.id,
        inputSlotId = printPort.id,
    )

    nodes.connections.add(c1)
    nodes.connections.add(c2)

    println(nodes)
    runGraph(nodes)

    println(json.serialize(nodes))
}


fun runGraph(graph: Nodes) {
    println(graph)
}