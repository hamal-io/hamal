package io.hamal.lib.nodes.compiler.graph

import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.nodes.AbstractUnitTest
import io.hamal.lib.nodes.NodeType.Companion.NodeType
import io.hamal.lib.nodes.NodeVersion.Companion.NodeVersion
import io.hamal.lib.nodes.compiler.graph.ComputationGraph.Companion.ComputationGraph
import io.hamal.lib.nodes.compiler.node.AbstractNode
import io.hamal.lib.nodes.compiler.node.NodeCompilerRegistry
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal object NodeCompilerTest : AbstractUnitTest() {

    @Test
    fun `Compiles graph without any nodes`() {
        testInstance.compile(ComputationGraph(graph()))
            .also { result -> assertThat(result, empty()) }
    }

    @Test
    fun `Compiles empty node without inputs and outputs`() {
        val result = testInstance.compile(ComputationGraph(graph(listOf(node(1, "Empty")))))
        assertThat(result, hasSize(1))

        val computedNode = result.first()
        assertThat(computedNode.outputs, empty())
        assertThat(computedNode.code, equalTo(ValueCode("function n_1()\n\nend\n")))
    }

    private val testInstance = NodeCompilerImpl(
        NodeCompilerRegistry(
            listOf(
                object : AbstractNode() {
                    override val type = NodeType("Empty")
                    override val version = NodeVersion("0.0.1")

                    override fun toCode(ctx: Context): ValueCode {
                        return ValueCode("""""")
                    }
                }
            )
        )
    )
}