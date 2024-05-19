package io.hamal.lib.nodes.compiler.graph

import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.nodes.AbstractUnitTest
import io.hamal.lib.nodes.Form
import io.hamal.lib.nodes.NodeType.Companion.NodeType
import io.hamal.lib.nodes.compiler.node.AbstractNode
import io.hamal.lib.nodes.compiler.node.NodeCompilerRegistry
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal object NodeCompilerTest : AbstractUnitTest() {

    @Test
    fun `Compiles graph without any nodes`() {
        testInstance.compile(graph())
            .also { result -> assertThat(result, empty()) }
    }

    @Test
    fun `Compiles empty node without inputs and outputs`() {
        testInstance.compile(
            graph(listOf(node(1, "Empty")))
        ).also { result ->
            assertThat(result, hasSize(1))

            val computedNode = result.first()
            assertThat(computedNode.inputs, empty())
            assertThat(computedNode.outputs, empty())

            assertThat(
                computedNode.code, equalTo(
                    ValueCode("function n_1()\n\nend\n")
                )
            )
        }
    }

    @Test
    fun test() {
        testInstance.compile(
            graph(
                listOf(
                    node(1, "IN_1_OUT_0")
                )
            )
        ).also { result ->
            assertThat(result, hasSize(1))
        }
    }

    private val testInstance = NodeCompilerImpl(
        NodeCompilerRegistry(
            listOf(
                object : AbstractNode() {
                    override val type = NodeType("Empty")
                    override val inputs: List<Form> = listOf()
                    override val outputs: List<Form> = listOf()

                    override fun toCode(ctx: Context): ValueCode {
                        return ValueCode("""""")
                    }
                },
                object : AbstractNode() {
                    override val type = NodeType("IN_1_OUT_0")
                    override val inputs: List<Form> = listOf()
                    override val outputs: List<Form> = listOf()

                    override fun toCode(ctx: Context): ValueCode {
                        return ValueCode("""""")
                    }
                }
            )
        )
    )
}