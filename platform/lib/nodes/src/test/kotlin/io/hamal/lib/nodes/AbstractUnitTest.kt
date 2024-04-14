package io.hamal.lib.nodes

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.nodes.control.Control


internal abstract class AbstractUnitTest {

    fun node(
        id: Long,
        type: String,
        controls: List<Control>,
        outputs: List<PortOutput> = listOf(),
        title: NodeTitle = NodeTitle("Title of ${id.toString(16)}"),
        position: Position = Position(0, 0),
        size: Size = Size(200, 200)
    ): Node {
        return Node(
            id = NodeId(SnowflakeId(id)),
            type = NodeType(type),
            title = title,
            position = position,
            size = size,
            controls = controls,
            outputs = outputs
        )
    }

    fun connection(
        id: Long,
        outputNode: Long,
        outputPort: Long,
        inputNode: Long,
        inputPort: Long
    ): Connection {
        return Connection(
            id = ConnectionId(SnowflakeId(id)),
            outputNode = Connection.Node(NodeId(SnowflakeId(outputNode))),
            outputPort = Connection.Port(id = PortId(SnowflakeId(outputPort))),
            inputNode = Connection.Node(NodeId(SnowflakeId(inputNode))),
            inputPort = Connection.Port(id = PortId(SnowflakeId(inputPort)))
        )
    }

}