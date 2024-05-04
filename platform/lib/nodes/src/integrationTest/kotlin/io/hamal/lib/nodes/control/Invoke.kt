package io.hamal.lib.nodes.control

import io.hamal.lib.common.value.TypeString
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.nodes.*
import io.hamal.lib.nodes.NodeId.Companion.NodeId
import io.hamal.lib.nodes.PortId.Companion.PortId
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test


internal object ControlInvokeTest : AbstractIntegrationTest() {

    @Test
    fun `A node can be invoked without exchanging any data`() {
        createTestRunner().run(
            unitOfWork(
                initValue = ValueString("This value will not passed into capture node"),
                graph = NodesGraph(
                    nodes = listOf(
                        node(1, "Init", listOf(PortOutput(PortId(20), TypeString))),
                        node(2, "Capture", listOf(PortOutput(PortId(22), TypeString)))
                    ),
                    connections = listOf(
                        connection(100, 1, 20, 2, 21)
                    ),
                    controls = listOf(
                        ControlInit(nextControlIdentifier(), NodeId(1)),
                        ControlInvoke(nextControlIdentifier(), NodeId(2), portInput(21, TypeString)),
                        ControlTextArea(
                            nextControlIdentifier(),
                            NodeId(2),
                            portInput(22, TypeString),
                            ValueString("default capture string")
                        ),
                    )
                )
            )
        )

        assertThat(testContext.captorOne.resultString, equalTo(ValueString("default capture string")))
    }

}