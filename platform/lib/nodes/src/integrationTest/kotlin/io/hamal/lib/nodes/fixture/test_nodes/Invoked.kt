package io.hamal.lib.nodes.fixture.test_nodes

import io.hamal.lib.common.hot.HotString
import io.hamal.lib.nodes.*
import io.hamal.lib.nodes.control.Control
import io.hamal.lib.nodes.generator.Generator
import io.hamal.lib.typesystem.type.Type
import io.hamal.lib.typesystem.type.TypeString
import io.hamal.lib.typesystem.value.ValueString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

object GeneratorInvoked : Generator {
    override val type: NodeType get() = NodeType("INVOKED")
    override val inputTypes: List<Type> get() = listOf(TypeString)
    override val outputTypes: List<Type> get() = listOf()

    override fun toCode(node: Node, controls: List<Control>): String {
        return """
            test = require_plugin('test')
            test.invoked()
            return 
        """.trimIndent()
    }
}

internal class InvokedTest : AbstractIntegrationTest() {

    @Test
    fun `Nodes invokes another node`() {

        createTestRunner().run(
            unitOfWork(
                initValue = HotString("Hamal Rocks"),
                graph = NodesGraph(
                    nodes = listOf(
                        node(1, "INIT", listOf(PortOutput(PortId(20), TypeString))),
                        node(2, "INVOKED")
                    ),
                    connections = listOf(
                        connection(100, 1, 20, 2, 21)
                    ),
                    controls = listOf(ControlTextArea(nextControlId(), NodeId(2), PortInput(PortId(21), TypeString), ValueString("default")))
                )
            )
        )

        assertThat(testInvoked.invocations, equalTo(1))
    }

    @Test
    fun `Nodes invokes multiple nodes`() {
        createTestRunner().run(
            unitOfWork(
                initValue = HotString("Hamal Rocks"),
                graph = NodesGraph(
                    nodes = listOf(
                        node(1, "INIT", listOf(PortOutput(PortId(20), TypeString))),
                        node(2, "INVOKED"),
                        node(3, "INVOKED")
                    ),
                    connections = listOf(
                        connection(100, 1, 20, 2, 21),
                        connection(100, 1, 20, 3, 22),
                    ),
                    controls = listOf(
                        ControlTextArea(nextControlId(), NodeId(2), PortInput(PortId(21), TypeString), ValueString("default")),
                        ControlTextArea(nextControlId(), NodeId(3), PortInput(PortId(22), TypeString), ValueString("default"))
                    )

                )
            )
        )

        assertThat(testInvoked.invocations, equalTo(2))
    }
}