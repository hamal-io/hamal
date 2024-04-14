package io.hamal.lib.nodes.fixture.test_nodes

import io.hamal.lib.common.hot.HotString
import io.hamal.lib.nodes.*
import io.hamal.lib.nodes.control.ControlInputString
import io.hamal.lib.nodes.generator.Generator
import io.hamal.lib.typesystem.TypeNew
import io.hamal.lib.typesystem.TypeString
import io.hamal.lib.typesystem.value.ValueString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

object GeneratorInvoked : Generator {
    override val type: NodeType get() = NodeType("INVOKED")
    override val inputTypes: List<TypeNew> get() = listOf(TypeString)
    override val outputTypes: List<TypeNew> get() = listOf()

    override fun toCode(node: Node): String {
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
                        node(1, "INIT", listOf(), listOf(PortOutput(PortId(20), TypeString))),
                        node(2, "INVOKED", listOf(ControlInputString(PortInput(PortId(21), TypeString), ValueString("default"))))
                    ),
                    connections = listOf(
                        connection(100, 1, 20, 2, 21)
                    )
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
                        node(1, "INIT", listOf(), listOf(PortOutput(PortId(20), TypeString))),
                        node(2, "INVOKED", listOf(ControlInputString(PortInput(PortId(21), TypeString), ValueString("default")))),
                        node(3, "INVOKED", listOf(ControlInputString(PortInput(PortId(22), TypeString), ValueString("default"))))
                    ),
                    connections = listOf(
                        connection(100, 1, 20, 2, 21),
                        connection(100, 1, 20, 3, 22),
                    )
                )
            )
        )

        assertThat(testInvoked.invocations, equalTo(2))
    }
}