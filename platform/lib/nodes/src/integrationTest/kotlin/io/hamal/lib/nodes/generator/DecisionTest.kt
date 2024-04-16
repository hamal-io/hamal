package io.hamal.lib.nodes.generator

import io.hamal.lib.common.hot.HotBoolean
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.hot.HotString
import io.hamal.lib.nodes.*
import io.hamal.lib.nodes.control.ControlInvoke
import io.hamal.lib.typesystem.type.TypeBoolean
import io.hamal.lib.typesystem.value.ValueFalse
import io.hamal.lib.typesystem.value.ValueTrue
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class DecisionTest : AbstractIntegrationTest() {

    @Test
    @Disabled
    fun `Boolean - happy path`() {
        createTestRunner().run(
            unitOfWork(
                initValue = HotBoolean(true),
                graph = NodesGraph(
                    nodes = listOf(
                        node(1, "Init", listOf(PortOutput(PortId(20), TypeBoolean))),
                        node(
                            2, "Decision", listOf(
                                portOutput(21, TypeBoolean),
                                portOutput(22, TypeBoolean)
                            )
                        ),
                        node(3, "Test_Capture", listOf(portOutput(23, TypeBoolean)), HotObject.builder().set("capture_fn", HotString("captureOne")).build()),
                        node(4, "Test_Invoked", listOf(), HotObject.builder().set("invoke_fn", HotString("invokeOne")).build()),
                        node(5, "Test_Capture", listOf(portOutput(25, TypeBoolean)), HotObject.builder().set("capture_fn", HotString("captureTwo")).build()),
                        node(6, "Test_Invoked", listOf(), HotObject.builder().set("invoke_fn", HotString("invokeTwo")).build())
                    ),
                    connections = listOf(
                        connection(100, 1, 20, 2, 30),
                        connection(101, 2, 21, 3, 31, "yes"),
                        connection(102, 2, 22, 4, 32, "yes"),
                        connection(103, 2, 22, 5, 33, "no"),
                        connection(104, 2, 22, 6, 34, "no"),
                    ),
                    controls = listOf(
                        ControlCheckbox(nextControlId(), NodeId(2), portInput(30, TypeBoolean), ValueTrue),
                        ControlCapture(nextControlId(), NodeId(3), portInput(31, TypeBoolean)),
                        ControlInvoke(nextControlId(), NodeId(4), portInput(32, TypeBoolean)),
                        ControlCapture(nextControlId(), NodeId(5), portInput(33, TypeBoolean)),
                        ControlInvoke(nextControlId(), NodeId(6), portInput(34, TypeBoolean))
                    )
                )
            )
        )

        assertThat(testContext.captorOne.resultBoolean, equalTo(ValueTrue))
        assertThat(testContext.invokedOne.invocations, equalTo(1))
        assertThat(testContext.captorTwo.resultBoolean, nullValue())
        assertThat(testContext.invokedTwo.invocations, equalTo(0))
    }

    @Test
    @Disabled
    fun `Boolean - sad path`() {
        createTestRunner().run(
            unitOfWork(
                initValue = HotBoolean(false),
                graph = NodesGraph(
                    nodes = listOf(
                        node(1, "Init", listOf(PortOutput(PortId(20), TypeBoolean))),
                        node(
                            2, "Decision", listOf(
                                portOutput(21, TypeBoolean),
                                portOutput(22, TypeBoolean)
                            )
                        ),
                        node(3, "Test_Capture", listOf(portOutput(23, TypeBoolean)), HotObject.builder().set("capture_fn", HotString("captureOne")).build()),
                        node(4, "Test_Invoked", listOf(), HotObject.builder().set("invoke_fn", HotString("invokeOne")).build()),
                        node(5, "Test_Capture", listOf(portOutput(25, TypeBoolean)), HotObject.builder().set("capture_fn", HotString("captureTwo")).build()),
                        node(6, "Test_Invoked", listOf(), HotObject.builder().set("invoke_fn", HotString("invokeTwo")).build())
                    ),
                    connections = listOf(
                        connection(100, 1, 20, 2, 30),
                        connection(101, 2, 21, 3, 31, "yes"),
                        connection(102, 2, 22, 4, 32, "yes"),
                        connection(103, 2, 22, 5, 33, "no"),
                        connection(104, 2, 22, 6, 34, "no"),
                    ),
                    controls = listOf(
                        ControlCheckbox(nextControlId(), NodeId(2), portInput(30, TypeBoolean), ValueTrue),
                        ControlCapture(nextControlId(), NodeId(3), portInput(31, TypeBoolean)),
                        ControlInvoke(nextControlId(), NodeId(4), portInput(32, TypeBoolean)),
                        ControlCapture(nextControlId(), NodeId(5), portInput(33, TypeBoolean)),
                        ControlInvoke(nextControlId(), NodeId(6), portInput(34, TypeBoolean))
                    )
                )
            )
        )

        assertThat(testContext.captorOne.resultBoolean, nullValue())
        assertThat(testContext.invokedOne.invocations, equalTo(0))
        assertThat(testContext.captorTwo.resultBoolean, equalTo(ValueFalse))
        assertThat(testContext.invokedTwo.invocations, equalTo(1))
    }
}