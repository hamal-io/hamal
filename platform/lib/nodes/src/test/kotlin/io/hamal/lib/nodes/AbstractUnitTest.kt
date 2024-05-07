package io.hamal.lib.nodes

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.nodes.ConnectionId.Companion.ConnectionId
import io.hamal.lib.nodes.ControlIdentifier.Companion.ControlIdentifier
import io.hamal.lib.nodes.NodeId.Companion.NodeId
import io.hamal.lib.nodes.NodeTitle.Companion.NodeTitle
import io.hamal.lib.nodes.NodeType.Companion.NodeType
import io.hamal.lib.nodes.PortId.Companion.PortId


internal abstract class AbstractUnitTest {

    fun node(
        id: Long,
        type: String,
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

    protected val nextControlIdentifier = NextControlIdentifier

    object NextControlIdentifier {

        operator fun invoke(): ControlIdentifier {
            return ControlIdentifier((counter++).toString(16))
        }

        private var counter: Int = 0
    }
}