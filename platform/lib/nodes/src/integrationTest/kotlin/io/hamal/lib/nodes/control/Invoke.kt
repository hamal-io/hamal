package io.hamal.lib.nodes.control

import io.hamal.lib.common.hot.HotString
import io.hamal.lib.nodes.*
import io.hamal.lib.typesystem.type.TypeString
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
                        node(1, "Init", listOf(PortOutput(PortId(20), TypeString))),
                        node(2, "Test_Capture", listOf(PortOutput(PortId(22), TypeString)))
                    ),
                    connections = listOf(
                        connection(100, 1, 20, 2, 21)
                    ),
                    controls = listOf(
                        ControlInit(nextControlId(), NodeId(1)),
                        ControlInvoke(nextControlId(), NodeId(2), portInput(21, TypeString)),
                        ControlTextArea(nextControlId(), NodeId(2), portInput(22, TypeString), ValueString("default capture string")),
                    )
                )
            )
        )

        assertThat(testContext.captorOne.resultString, equalTo(ValueString("default capture string")))
    }

}