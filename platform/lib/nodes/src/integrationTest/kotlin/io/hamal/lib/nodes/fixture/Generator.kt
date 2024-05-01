package io.hamal.lib.nodes.fixture

import io.hamal.lib.common.hot.HotString
import io.hamal.lib.common.value.*
import io.hamal.lib.nodes.*
import io.hamal.lib.nodes.NodeType.Companion.NodeType
import io.hamal.lib.nodes.control.Control
import io.hamal.lib.nodes.generator.Generator
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test


interface GeneratorCapture : Generator {
    override val type: NodeType get() = NodeType("Test_Capture")

    object Boolean : GeneratorCapture {
        override val inputTypes: List<Type> get() = listOf(TypeBoolean)
        override val outputTypes: List<Type> get() = listOf(TypeBoolean)
        override fun toCode(node: Node, controls: List<Control>) = captureCode(node, controls)

    }

    object Decimal : GeneratorCapture {
        override val inputTypes: List<Type> get() = listOf(TypeDecimal)
        override val outputTypes: List<Type> get() = listOf(TypeDecimal)
        override fun toCode(node: Node, controls: List<Control>) = captureCode(node, controls)

    }

    object Number : GeneratorCapture {
        override val inputTypes: List<Type> get() = listOf(TypeNumber)
        override val outputTypes: List<Type> get() = listOf(TypeNumber)
        override fun toCode(node: Node, controls: List<Control>) = captureCode(node, controls)

    }

    object String : GeneratorCapture {
        override val inputTypes: List<Type> get() = listOf(TypeString)
        override val outputTypes: List<Type> get() = listOf(TypeString)
        override fun toCode(node: Node, controls: List<Control>) = captureCode(node, controls)
    }

    fun captureCode(node: Node, controls: List<Control>): kotlin.String {
        val captureFunction = node.properties.value.find("capture_fn")?.stringValue ?: "captureOne"
        return """
            test = require_plugin('test')
            test.$captureFunction(arg_1)
            return arg_1
        """.trimIndent()
    }
}


sealed interface GeneratorInvoked : Generator {
    override val type: NodeType get() = NodeType("Test_Invoked")

    data object Empty : GeneratorInvoked {
        override val inputTypes: List<Type> get() = listOf()
        override val outputTypes: List<Type> get() = listOf()

        override fun toCode(node: Node, controls: List<Control>): kotlin.String {
            val invokeFunction = node.properties.value.find("invoke_fn")?.stringValue ?: "invokeOne"
            return """
            test = require_plugin('test')
            test.$invokeFunction()
            return 
        """.trimIndent()
        }
    }

    data object Boolean : GeneratorInvoked {
        override val inputTypes: List<Type> get() = listOf(TypeBoolean)
        override val outputTypes: List<Type> get() = listOf()

        override fun toCode(node: Node, controls: List<Control>): kotlin.String {
            val invokeFunction = node.properties.value.find("invoke_fn")?.stringValue ?: "invokeOne"
            return """
            test = require_plugin('test')
            test.$invokeFunction()
            return 
        """.trimIndent()
        }
    }

    data object String : GeneratorInvoked {
        override val inputTypes: List<Type> get() = listOf(TypeString)
        override val outputTypes: List<Type> get() = listOf()

        override fun toCode(node: Node, controls: List<Control>): kotlin.String {
            val invokeFunction = node.properties.value.find("invoke_fn")?.stringValue ?: "invokeOne"

            return """
            test = require_plugin('test')
            test.$invokeFunction()
            return 
        """.trimIndent()
        }
    }
}

internal class TestInvokedTest : AbstractIntegrationTest() {

    @Test
    fun `Nodes invokes another node`() {

        createTestRunner().run(
            unitOfWork(
                initValue = HotString("Hamal Rocks"),
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
                initValue = HotString("Hamal Rocks"),
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