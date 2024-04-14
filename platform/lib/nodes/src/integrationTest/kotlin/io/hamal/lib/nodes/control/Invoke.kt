package io.hamal.lib.nodes.control

import io.hamal.lib.common.hot.HotString
import io.hamal.lib.nodes.*
import io.hamal.lib.typesystem.TypeString
import io.hamal.lib.typesystem.value.ValueString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test


internal object ControlInvokeTest : AbstractIntegrationTest() {

    @Test
    fun `A node can be invoked without exchanging any data`() {
        createTestRunner().run(
            unitOfWork(
                initValue = HotString("This value will not passed into capture node"),
                graph = NodesGraph(
                    nodes = listOf(
                        node(1, "INIT", listOf(PortOutput(PortId(20), TypeString))),
                        node(2, "CAPTURE", listOf(PortOutput(PortId(22), TypeString)))
                    ),
                    connections = listOf(
                        connection(100, 1, 20, 2, 21)
                    ),
                    controls = listOf(
                        ControlInit(nextControlId(), NodeId(1)),
                        ControlInvoke(nextControlId(), NodeId(2), PortId(21)),
                        ControlInputString(nextControlId(), NodeId(2), PortInput(PortId(22), TypeString), ValueString("default capture string"))
                    )
                )
            )
        )

        assertThat(testCaptor1.resultString, equalTo(ValueString("default capture string")))
    }

}