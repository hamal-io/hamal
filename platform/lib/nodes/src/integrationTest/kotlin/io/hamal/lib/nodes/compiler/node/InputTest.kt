package io.hamal.lib.nodes.compiler.node

import io.hamal.lib.common.value.*
import io.hamal.lib.nodes.*
import io.hamal.lib.nodes.AbstractIntegrationTest
import io.hamal.lib.nodes.NodeId.Companion.NodeId
import io.hamal.lib.nodes.PortId.Companion.PortId
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

internal object InputTest : AbstractIntegrationTest() {

    @Test
    fun `Boolean - true`() {
        runTest(
            unitOfWork(
                graph = NodesGraph(
                    nodes = listOf(
                        node(1, "Input", listOf(PortOutput(PortId(20), TypeBoolean))),
                        node(
                            2,
                            "Capture",
                            listOf(portOutput(22, TypeBoolean)),
                            ValueObject.builder().set("capture_fn", ValueString("capture_one")).build()
                        ),
                    ),
                    connections = listOf(
                        connection(100, 1, 20, 2, 30),
                    ),
                    controls = listOf(
                        ControlBooleanInput(nextControlIdentifier(), NodeId(1), ValueTrue),
                        ControlCapture(nextControlIdentifier(), NodeId(2), portInput(31, TypeBoolean)),
                    )
                )
            )
        )
        assertThat(testContext.captorOne.resultBoolean, equalTo(ValueTrue))
    }

    @Test
    fun `Boolean - false`() {
        runTest(
            unitOfWork(
                graph = NodesGraph(
                    nodes = listOf(
                        node(1, "Input", listOf(PortOutput(PortId(20), TypeBoolean))),
                        node(
                            2,
                            "Capture",
                            listOf(portOutput(22, TypeBoolean)),
                            ValueObject.builder().set("capture_fn", ValueString("capture_one")).build()
                        ),
                    ),
                    connections = listOf(
                        connection(100, 1, 20, 2, 30),
                    ),
                    controls = listOf(
                        ControlBooleanInput(nextControlIdentifier(), NodeId(1), ValueFalse),
                        ControlCapture(nextControlIdentifier(), NodeId(2), portInput(31, TypeBoolean)),
                    )
                )
            )
        )
        assertThat(testContext.captorOne.resultBoolean, equalTo(ValueFalse))
    }

}