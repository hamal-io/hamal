package io.hamal.lib.nodes.fixture

import io.hamal.lib.common.value.*
import io.hamal.lib.nodes.AbstractIntegrationTest
import io.hamal.lib.nodes.ControlTextArea
import io.hamal.lib.nodes.NodeId.Companion.NodeId
import io.hamal.lib.nodes.NodeType
import io.hamal.lib.nodes.NodeType.Companion.NodeType
import io.hamal.lib.nodes.NodesGraph
import io.hamal.lib.nodes.compiler.node.NodeCompiler
import io.hamal.lib.nodes.compiler.node.NodeCompiler.Context
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test


interface Capture : NodeCompiler {
    override val type: NodeType get() = NodeType("Capture")

    object Boolean : Capture {
        override val inputTypes: List<ValueType> get() = listOf(TypeBoolean)
        override val outputTypes: List<ValueType> get() = listOf(TypeBoolean)
        override fun toCode(ctx: Context) = captureCode(ctx)

    }

    object Decimal : Capture {
        override val inputTypes: List<ValueType> get() = listOf(TypeDecimal)
        override val outputTypes: List<ValueType> get() = listOf(TypeDecimal)
        override fun toCode(ctx: Context) = captureCode(ctx)

    }

    object Number : Capture {
        override val inputTypes: List<ValueType> get() = listOf(TypeNumber)
        override val outputTypes: List<ValueType> get() = listOf(TypeNumber)
        override fun toCode(ctx: Context) = captureCode(ctx)

    }

    object String : Capture {
        override val inputTypes: List<ValueType> get() = listOf(TypeString)
        override val outputTypes: List<ValueType> get() = listOf(TypeString)
        override fun toCode(ctx: Context) = captureCode(ctx)
    }

    fun captureCode(ctx: Context): ValueCode {
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


sealed interface Invoked : NodeCompiler {
    override val type: NodeType get() = NodeType("Test_Invoked")

    data object Empty : Invoked {
        override val inputTypes: List<ValueType> get() = listOf()
        override val outputTypes: List<ValueType> get() = listOf()

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

    data object Boolean : Invoked {
        override val inputTypes: List<ValueType> get() = listOf(TypeBoolean)
        override val outputTypes: List<ValueType> get() = listOf()

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

    data object String : Invoked {
        override val inputTypes: List<ValueType> get() = listOf(TypeString)
        override val outputTypes: List<ValueType> get() = listOf()

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

        createTestRunner().run(
            unitOfWork(
                initValue = ValueString("Hamal Rocks"),
                graph = NodesGraph(
                    nodes = listOf(
                        node(1, "Init", listOf(portOutput(20, TypeString))),
                        node(2, "Test_Invoked")
                    ),
                    connections = listOf(
                        connection(100, 1, 20, 2, 21)
                    ),
                    controls = listOf(
                        ControlTextArea(
                            nextControlIdentifier(),
                            NodeId(2),
                            portInput(21, TypeString),
                            ValueString("default")
                        )
                    )
                )
            )
        )

        assertThat(testContext.invokedOne.invocations, equalTo(1))
    }

    @Test
    fun `Nodes invokes multiple nodes`() {
        createTestRunner().run(
            unitOfWork(
                initValue = ValueString("Hamal Rocks"),
                graph = NodesGraph(
                    nodes = listOf(
                        node(1, "Init", listOf(portOutput(20, TypeString))),
                        node(2, "Test_Invoked"),
                        node(3, "Test_Invoked")
                    ),
                    connections = listOf(
                        connection(100, 1, 20, 2, 21),
                        connection(100, 1, 20, 3, 22),
                    ),
                    controls = listOf(
                        ControlTextArea(
                            nextControlIdentifier(),
                            NodeId(2),
                            portInput(21, TypeString),
                            ValueString("default")
                        ),
                        ControlTextArea(
                            nextControlIdentifier(),
                            NodeId(3),
                            portInput(22, TypeString),
                            ValueString("default")
                        )
                    )

                )
            )
        )

        assertThat(testContext.invokedOne.invocations, equalTo(2))
    }
}