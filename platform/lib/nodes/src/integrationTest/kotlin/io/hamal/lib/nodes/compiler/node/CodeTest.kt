package io.hamal.lib.nodes.compiler.node

import io.hamal.lib.common.value.*
import io.hamal.lib.nodes.*
import io.hamal.lib.nodes.AbstractIntegrationTest
import io.hamal.lib.nodes.NodeId.Companion.NodeId
import io.hamal.lib.nodes.PortId.Companion.PortId
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test


internal class CodeTest : AbstractIntegrationTest() {

    @Test
    fun `Code invoked with object`() {
        runTest(
            unitOfWork(
                graph = NodesGraph(
                    nodes = listOf(
                        node(1, "Input", listOf(PortOutput(PortId(20), TypeString))),
                        node(2, "Code", listOf()),
                    ),
                    connections = listOf(connection(100, 1, 20, 2, 30)),
                    controls = listOf(
                        ControlInputString(nextControlId(), NodeId(1), portInput(23, TypeString), ValueString("0x001")),
                        ControlCode(
                            nextControlId(), NodeId(2), portInput(30, TypeString), ValueCode(
                                """
                                    test = require_plugin('test')
                                    test.capture_one(arg_1)
                                """.trimIndent()
                            )
                        ),
                    )
                )
            )
        )

        assertThat(testContext.captorOne.resultString, equalTo(ValueString("0x001")))
    }

}