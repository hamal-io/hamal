package io.hamal.lib.nodes.compiler.node

import io.hamal.lib.common.value.ValueFalse
import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.common.value.ValueTrue
import io.hamal.lib.nodes.*
import io.hamal.lib.nodes.NodeIndex.Companion.NodeIndex
import io.hamal.lib.nodes.PortIndex.Companion.PortIndex
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
                        node(1, "Input", listOf(PortOutput(PortIndex(20), Form.Boolean))),
                        node(
                            2,
                            "Capture",
                            listOf(),
                            ValueObject.builder().set("capture_fn", ValueString("capture_one")).build()
                        ),
                    ),
                    connections = listOf(connection(100, 1, 20, 2, 30)),
                    controls = listOf(
                        ControlInputBoolean(nextControlId(), NodeIndex(1), portInput(23, Form.Boolean), ValueTrue),
                        ControlInputFromPort(nextControlId(), NodeIndex(2), portInput(30, Form.Boolean)),
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
                        node(1, "Input", listOf(PortOutput(PortIndex(20), Form.Boolean))),
                        node(
                            2,
                            "Capture",
                            listOf(),
                            ValueObject.builder().set("capture_fn", ValueString("capture_one")).build()
                        ),
                    ),
                    connections = listOf(
                        connection(100, 1, 20, 2, 30),
                    ),
                    controls = listOf(
                        ControlInputBoolean(nextControlId(), NodeIndex(1), portInput(23, Form.Boolean), ValueFalse),
                        ControlInputFromPort(nextControlId(), NodeIndex(2), portInput(30, Form.Boolean)),
                    )
                )
            )
        )
        assertThat(testContext.captorOne.resultBoolean, equalTo(ValueFalse))
    }

    @Test
    fun `String`() {
        runTest(
            unitOfWork(
                graph = NodesGraph(
                    nodes = listOf(
                        node(1, "Input", listOf(PortOutput(PortIndex(20), Form.String))),
                        node(
                            2,
                            "Capture",
                            listOf(portOutput(22, Form.String)),
                            ValueObject.builder().set("capture_fn", ValueString("capture_one")).build()
                        ),
                    ),
                    connections = listOf(connection(100, 1, 20, 2, 30)),
                    controls = listOf(
                        ControlInputString(
                            nextControlId(),
                            NodeIndex(1),
                            portInput(23, Form.String),
                            ValueString("Hamal Rocks")
                        ),
                        ControlInputFromPort(nextControlId(), NodeIndex(2), portInput(30, Form.Boolean)),
                    )
                )
            )
        )
        assertThat(testContext.captorOne.resultString, equalTo(ValueString("Hamal Rocks")))
    }


}