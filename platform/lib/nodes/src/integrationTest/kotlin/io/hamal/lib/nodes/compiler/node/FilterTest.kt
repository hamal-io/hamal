package io.hamal.lib.nodes.compiler.node

import io.hamal.lib.common.value.*
import io.hamal.lib.nodes.*
import io.hamal.lib.nodes.NodeId.Companion.NodeId
import io.hamal.lib.nodes.PortId.Companion.PortId
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

internal class FilterTest : AbstractIntegrationTest() {

    @Test
    fun `Boolean - happy path`() {
        runTest(
            unitOfWork(
                initValue = ValueTrue,
                graph = NodesGraph(
                    nodes = listOf(
                        node(1, "Init", listOf(PortOutput(PortId(20), TypeBoolean))),
                        node(2, "Filter", listOf(portOutput(21, TypeBoolean))),
                        node(
                            3,
                            "Capture",
                            listOf(portOutput(23, TypeBoolean)),
                            ValueObject.builder().set("capture_fn", ValueString("capture_one")).build()
                        ),
                        node(
                            4,
                            "Test_Invoked",
                            listOf(),
                            ValueObject.builder().set("invoke_fn", ValueString("invoke_one")).build()
                        )
                    ),
                    connections = listOf(
                        connection(100, 1, 20, 2, 30),
                        connection(101, 2, 21, 3, 31),
                        connection(102, 2, 21, 4, 32),
                    ),
                    controls = listOf(
                        ControlInvoke(nextControlIdentifier(), NodeId(2), portInput(30, TypeBoolean)),
                        ControlInputBoolean(nextControlIdentifier(), NodeId(2), portInput(33, TypeBoolean), ValueTrue),
                        ControlCapture(nextControlIdentifier(), NodeId(3), portInput(31, TypeBoolean)),
                        ControlInvoke(nextControlIdentifier(), NodeId(4), portInput(32, TypeBoolean))
                    )
                )
            )
        )

        assertThat(testContext.captorOne.resultBoolean, equalTo(ValueTrue))
        assertThat(testContext.invokedOne.invocations, equalTo(1))
    }

    @Test
    fun `Boolean - sad path`() {
        runTest(
            unitOfWork(
                initValue = ValueFalse,
                graph = NodesGraph(
                    nodes = listOf(
                        node(1, "Init", listOf(PortOutput(PortId(20), TypeBoolean))),
                        node(2, "Filter", listOf(portOutput(21, TypeBoolean))),
                        node(3,"Capture",listOf(portOutput(23, TypeBoolean)),ValueObject.builder().set("capture_fn", ValueString("capture_one")).build()),
                        node(4,"Test_Invoked",listOf(),ValueObject.builder().set("invoke_fn", ValueString("invoke_one")).build())
                    ),
                    connections = listOf(
                        connection(100, 1, 20, 2, 30),
                        connection(101, 2, 21, 3, 31),
                        connection(102, 2, 21, 4, 32),
                    ),
                    controls = listOf(
                        ControlInvoke(nextControlIdentifier(), NodeId(2), portInput(30, TypeBoolean)),
                        ControlInputBoolean(nextControlIdentifier(), NodeId(2), portInput(33, TypeBoolean), ValueTrue),
                        ControlCapture(nextControlIdentifier(), NodeId(3), portInput(31, TypeBoolean)),
                        ControlInvoke(nextControlIdentifier(), NodeId(4), portInput(32, TypeBoolean))
                    )
                )
            )
        )

        assertThat(testContext.captorOne.resultBoolean, nullValue())
        assertThat(testContext.invokedOne.invocations, equalTo(0))
    }
}