package io.hamal.lib.nodes.compiler.graph

import io.hamal.lib.common.value.TypeString
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.nodes.*
import io.hamal.lib.nodes.NodeIndex.Companion.NodeIndex
import io.hamal.lib.nodes.PortIndex.Companion.PortIndex
import io.hamal.lib.nodes.compiler.graph.ComputationGraph.Companion.ComputationGraph
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
                    node(1, "Init", listOf(PortOutput(PortIndex(20), TypeString))),
                    node(2, "Test_Invoked")
                ),
                connections = listOf(
                    connection(100, 1, 20, 2, 21)
                ),
                controls = listOf(
                    ControlInputString(
                        nextControlIndex(),
                        NodeIndex(2),
                        PortInput(PortIndex(21), TypeString),
                        ValueString("default string")
                    )
                )
            )
        )

        assertThat(
            result, equalTo(
                ComputationGraph(
                    mapOf(
                        NodeIndex(1) to listOf(NodeIndex(2))
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
                    node(1, "A", listOf(PortOutput(PortIndex(20), TypeString))),
                    node(2, "B"),
                    node(3, "C")
                ),
                connections = listOf(
                    connection(100, 1, 20, 2, 21),
                    connection(101, 1, 20, 3, 22),
                ),
                controls = listOf(
                    ControlInputString(
                        nextControlIndex(),
                        NodeIndex(2),
                        PortInput(PortIndex(21), TypeString),
                        ValueString("default")
                    ),
                    ControlInputString(
                        nextControlIndex(),
                        NodeIndex(3),
                        PortInput(PortIndex(22), TypeString),
                        ValueString("default")
                    )
                )
            )
        )

        assertThat(
            result, equalTo(
                ComputationGraph(
                    mapOf(
                        NodeIndex(1) to listOf(NodeIndex(2), NodeIndex(3))
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
                    node(1, "Init", listOf(PortOutput(PortIndex(20), TypeString))),
                    node(2, "Test_Invoked"),
                    node(3, "Test_Invoked")
                ),
                connections = listOf(
                    connection(100, 1, 20, 3, 22),
                    connection(101, 2, 21, 3, 22),
                ),
                controls = listOf(
                    ControlInputString(
                        nextControlIndex(),
                        NodeIndex(2),
                        PortInput(PortIndex(21), TypeString),
                        ValueString("default")
                    ),
                    ControlInputString(
                        nextControlIndex(),
                        NodeIndex(3),
                        PortInput(PortIndex(22), TypeString),
                        ValueString("default")
                    )
                )
            )
        )

        assertThat(
            result, equalTo(
                ComputationGraph(
                    mapOf(
                        NodeIndex(1) to listOf(NodeIndex(3)),
                        NodeIndex(2) to listOf(NodeIndex(3))
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
        val testInstance = ComputationGraph(mapOf(NodeIndex(1) to listOf()))
        val result = breadthFirstSearch(testInstance, NodeIndex(111111))
        assertThat(result, equalTo(listOf()))
    }

    @Test
    fun `Single node`() {
        val testInstance = ComputationGraph(mapOf(NodeIndex(1) to listOf()))
        val result = breadthFirstSearch(testInstance, NodeIndex(1))
        assertThat(result, equalTo(listOf(NodeIndex(1))))
    }

    @Test
    fun `Visit graph`() {
        val testInstance = ComputationGraph(
            mapOf(
                NodeIndex(1) to listOf(NodeIndex(2), NodeIndex(3), NodeIndex(4)),
                NodeIndex(2) to listOf(NodeIndex(5), NodeIndex(6)),
                NodeIndex(3) to listOf(NodeIndex(6), NodeIndex(7)),
                NodeIndex(4) to listOf(NodeIndex(7), NodeIndex(8)),
                NodeIndex(5) to listOf(),
                NodeIndex(6) to listOf(),
                NodeIndex(7) to listOf(),
                NodeIndex(8) to listOf()
            )
        )
        val result = breadthFirstSearch(testInstance, NodeIndex(1))
        assertThat(
            result, equalTo(
                listOf(
                    NodeIndex(1), NodeIndex(2), NodeIndex(3), NodeIndex(4),
                    NodeIndex(5), NodeIndex(6), NodeIndex(7), NodeIndex(8)
                )
            )
        )
    }
}