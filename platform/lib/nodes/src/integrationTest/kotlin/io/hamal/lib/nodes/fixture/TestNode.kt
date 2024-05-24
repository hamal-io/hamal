package io.hamal.lib.nodes.fixture

import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.nodes.*
import io.hamal.lib.nodes.NodeIndex.Companion.NodeIndex
import io.hamal.lib.nodes.NodeType.Companion.NodeType
import io.hamal.lib.nodes.compiler.node.AbstractNode
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test


sealed class Capture : AbstractNode() {
    override val type: NodeType get() = NodeType("Capture")

    data object V_0_0_1 : Capture() {
        override val version = NodeVersion.v_0_0_1

        override fun toCode(ctx: Context): ValueCode {
            val captureFunction = ctx.node.properties.value.findString("capture_fn")?.stringValue ?: "capture_one"
            return ValueCode(
                """
            test = require_plugin('test')
            test.$captureFunction(arg_1)
            return arg_1
        """.trimIndent()
            )
        }
    }
}


sealed class Invoked : AbstractNode() {
    override val type: NodeType get() = NodeType("Test_Invoked")

    data object V_0_0_1 : Invoked() {
        override val version = NodeVersion.v_0_0_1

        override fun toCode(ctx: Context): ValueCode {
            val invokeFunction = ctx.node.properties.value.findString("invoke_fn")?.stringValue ?: "invoke_one"
            return ValueCode(
                """
            test = require_plugin('test')
            test.$invokeFunction()
            return
        """.trimIndent()
            )
        }
    }
}

internal class TestInvokedTest : AbstractIntegrationTest() {

    @Test
    fun `Nodes invokes another node`() {

        runTest(
            unitOfWork(
                graph = NodesGraph(
                    nodes = listOf(
                        node(1, "Start", listOf(portOutput(20, Form.String))),
                        node(2, "Test_Invoked")
                    ),
                    connections = listOf(
                        connection(100, 1, 20, 2, 21)
                    ),
                    controls = listOf(
                        ControlInputString(
                            nextControlId(),
                            NodeIndex(1),
                            portInput(-1, Form.String),
                            ValueString("Hamal Rocks")
                        ),
                        ControlInputString(nextControlId(), NodeIndex(2), portInput(21, Form.String), ValueString(""))
                    )
                )
            )
        )

        assertThat(testContext.invokedOne.invocations, equalTo(1))
    }

    @Test
    fun `Nodes invokes multiple nodes`() {
        runTest(
            unitOfWork(
                graph = NodesGraph(
                    nodes = listOf(
                        node(1, "Start", listOf(portOutput(20, Form.String))),
                        node(2, "Test_Invoked"),
                        node(3, "Test_Invoked")
                    ),
                    connections = listOf(
                        connection(100, 1, 20, 2, 21),
                        connection(100, 1, 20, 3, 22),
                    ),
                    controls = listOf(
                        ControlInputString(
                            nextControlId(),
                            NodeIndex(1),
                            portInput(-1, Form.String),
                            ValueString("Hamal Rocks")
                        ),
                        ControlInputString(
                            nextControlId(),
                            NodeIndex(2),
                            portInput(21, Form.String),
                            ValueString("default")
                        ),
                        ControlInputString(
                            nextControlId(),
                            NodeIndex(3),
                            portInput(22, Form.String),
                            ValueString("default")
                        )
                    )

                )
            )
        )

        assertThat(testContext.invokedOne.invocations, equalTo(2))
    }
}