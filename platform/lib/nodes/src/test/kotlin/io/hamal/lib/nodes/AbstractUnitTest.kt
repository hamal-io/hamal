package io.hamal.lib.nodes

import io.hamal.lib.nodes.ConnectionIndex.Companion.ConnectionIndex
import io.hamal.lib.nodes.ControlIndex.Companion.ControlIndex
import io.hamal.lib.nodes.NodeIndex.Companion.NodeIndex
import io.hamal.lib.nodes.NodeTitle.Companion.NodeTitle
import io.hamal.lib.nodes.NodeType.Companion.NodeType
import io.hamal.lib.nodes.NodeVersion.Companion.NodeVersion
import io.hamal.lib.nodes.PortIndex.Companion.PortIndex
import io.hamal.lib.nodes.PortKey.Companion.PortKey


internal abstract class AbstractUnitTest {

    fun node(
        id: Long,
        type: String,
        outputs: List<PortOutput> = listOf(),
        title: NodeTitle = NodeTitle("Title of ${id.toString(16)}"),
        position: Position = Position(0.0, 0.0),
        size: Size = Size(200, 200)
    ): Node {
        return Node(
            index = NodeIndex(id),
            type = NodeType(type),
            version = NodeVersion("0.0.1"),
            title = title,
            position = position,
            size = size,
            outputs = outputs
        )
    }

    fun graph(
        nodes: List<Node> = listOf(),
        connections: List<Connection> = listOf(),
        controls: List<Control> = listOf(),
    ) = NodesGraph(nodes, connections, controls)

    fun connection(
        id: Long,
        outputNode: Long,
        outputPort: Long,
        inputNode: Long,
        inputPort: Long
    ): Connection {
        return Connection(
            index = ConnectionIndex(id),
            outputNode = Connection.Node(NodeIndex(outputNode)),
            outputPort = Connection.Port(PortIndex(outputPort), PortKey(outputPort.toString(16))),
            inputNode = Connection.Node(NodeIndex(inputNode)),
            inputPort = Connection.Port(PortIndex(inputPort), PortKey(inputPort.toString(16)))
        )
    }

    protected val nextControlIndex = NextControlIndex

    object NextControlIndex {

        operator fun invoke(): ControlIndex {
            return ControlIndex(counter++)
        }

        private var counter: Int = 0
    }
}