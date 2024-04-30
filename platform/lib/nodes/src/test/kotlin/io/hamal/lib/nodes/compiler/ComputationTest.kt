package io.hamal.lib.nodes.compiler

import io.hamal.lib.nodes.*
import io.hamal.lib.nodes.compiler.ComputationGraph.Companion.ComputationGraph
import io.hamal.lib.value.type.TypeString
import io.hamal.lib.value.ValueString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

internal object ComputationNodesGraphTest : AbstractUnitTest() {

    @Test
    fun `Computation graph of empty nodes graph`() {
        val result = ComputationGraph(NodesGraph(nodes = listOf(), connections = listOf(), controls = listOf()))
        assertThat(result, equalTo(ComputationGraph(mapOf())))
    }

    @Test
    fun `Computation graph of not connected nodes`() {
        val result = ComputationGraph(
            NodesGraph(
                nodes = listOf(
                    node(1, "Init", listOf(PortOutput(PortId(20), TypeString))),
                    node(2, "Test_Invoked")
                ),
                connections = listOf(
                    connection(100, 1, 20, 2, 21)
                ),
                controls = listOf(
                    ControlTextArea(
                        nextControlIdentifier(),
                        NodeId(2),
                        PortInput(PortId(21), TypeString),
                        ValueString("default string")
                    )
                )
            )
        )

        assertThat(
            result, equalTo(
                ComputationGraph(
                    mapOf(
                        NodeId(1) to listOf(NodeId(2))
                    )
                )
            )
        )
    }

    @Test
    fun `Computation graph of fan out connection`() {
        val result = ComputationGraph(
            graph = NodesGraph(
                nodes = listOf(
                    node(1, "A", listOf(PortOutput(PortId(20), TypeString))),
                    node(2, "B"),
                    node(3, "C")
                ),
                connections = listOf(
                    connection(100, 1, 20, 2, 21),
                    connection(101, 1, 20, 3, 22),
                ),
                controls = listOf(
                    ControlTextArea(
                        nextControlIdentifier(),
                        NodeId(2),
                        PortInput(PortId(21), TypeString),
                        ValueString("default")
                    ),
                    ControlTextArea(
                        nextControlIdentifier(),
                        NodeId(3),
                        PortInput(PortId(22), TypeString),
                        ValueString("default")
                    )
                )
            )
        )

        assertThat(
            result, equalTo(
                ComputationGraph(
                    mapOf(
                        NodeId(1) to listOf(NodeId(2), NodeId(3))
                    )
                )
            )
        )
    }

    @Test
    fun `Computation graph of fan int connection`() {
        val result = ComputationGraph(
            graph = NodesGraph(
                nodes = listOf(
                    node(1, "Init", listOf(PortOutput(PortId(20), TypeString))),
                    node(2, "Test_Invoked"),
                    node(3, "Test_Invoked")
                ),
                connections = listOf(
                    connection(100, 1, 20, 3, 22),
                    connection(101, 2, 21, 3, 22),
                ),
                controls = listOf(
                    ControlTextArea(
                        nextControlIdentifier(),
                        NodeId(2),
                        PortInput(PortId(21), TypeString),
                        ValueString("default")
                    ),
                    ControlTextArea(
                        nextControlIdentifier(),
                        NodeId(3),
                        PortInput(PortId(22), TypeString),
                        ValueString("default")
                    )
                )
            )
        )

        assertThat(
            result, equalTo(
                ComputationGraph(
                    mapOf(
                        NodeId(1) to listOf(NodeId(3)),
                        NodeId(2) to listOf(NodeId(3))
                    )
                )
            )
        )
    }


    @Test
    fun `Computation graph with single connection`() {
        val result = ComputationGraph(NodesGraph(nodes = listOf(), connections = listOf(), controls = listOf()))
        assertThat(result, equalTo(ComputationGraph(mapOf())))
    }

}

internal object BreadthFirstSearchTest {

    @Test
    fun `Root node does not exist`() {
        val testInstance = ComputationGraph(mapOf(NodeId(1) to listOf()))
        val result = breadthFirstSearch(testInstance, NodeId(111111))
        assertThat(result, equalTo(listOf()))
    }

    @Test
    fun `Single node`() {
        val testInstance = ComputationGraph(mapOf(NodeId(1) to listOf()))
        val result = breadthFirstSearch(testInstance, NodeId(1))
        assertThat(result, equalTo(listOf(NodeId(1))))
    }

    @Test
    fun `Visit graph`() {
        val testInstance = ComputationGraph(
            mapOf(
                NodeId(1) to listOf(NodeId(2), NodeId(3), NodeId(4)),
                NodeId(2) to listOf(NodeId(5), NodeId(6)),
                NodeId(3) to listOf(NodeId(6), NodeId(7)),
                NodeId(4) to listOf(NodeId(7), NodeId(8)),
                NodeId(5) to listOf(),
                NodeId(6) to listOf(),
                NodeId(7) to listOf(),
                NodeId(8) to listOf()
            )
        )
        val result = breadthFirstSearch(testInstance, NodeId(1))
        assertThat(
            result, equalTo(
                listOf(
                    NodeId(1), NodeId(2), NodeId(3), NodeId(4),
                    NodeId(5), NodeId(6), NodeId(7), NodeId(8)
                )
            )
        )
    }
}