package io.hamal.lib.nodes.compiler.node

import io.hamal.lib.common.value.*
import io.hamal.lib.domain.vo.ExecStatusCode.Companion.ExecStatusCode
import io.hamal.lib.nodes.*
import io.hamal.lib.nodes.NodeId.Companion.NodeId
import io.hamal.runner.test.TestFailConnector
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test


internal class InitTest : AbstractIntegrationTest() {

    @Test
    fun `Able to receive value with default selector`() {
        runTest(
            unitOfWork(
                initValue = ValueString("Hamal Rocks"),
                graph = NodesGraph(
                    nodes = listOf(
                        node(1, "Init", listOf(portOutput(20, TypeString))),
                        node(2, "Capture", listOf(portOutput(22, TypeString)))
                    ),
                    connections = listOf(
                        connection(100, 1, 20, 2, 21)
                    ),
                    controls = listOf(
                        ControlInit(nextControlIdentifier(), NodeId(1), ControlInit.Config()),
                        ControlTextArea(
                            nextControlIdentifier(),
                            NodeId(2),
                            portInput(21, TypeString),
                            ValueString("default string")
                        )
                    )
                )
            )
        )

        assertThat(testContext.captorOne.resultString, equalTo(ValueString("Hamal Rocks")))
    }

    @Test
    fun `Boolean`() {
        runTest(
            unitOfWork(
                initValue = ValueTrue,
                graph = NodesGraph(
                    nodes = listOf(
                        node(1, "Init", listOf(portOutput(20, TypeBoolean))),
                        node(2, "Capture", listOf(portOutput(22, TypeBoolean)))
                    ),
                    connections = listOf(
                        connection(100, 1, 20, 2, 21)
                    ),
                    controls = listOf(
                        ControlCheckbox(nextControlIdentifier(), NodeId(2), portInput(21, TypeBoolean), ValueFalse)
                    )
                )
            )
        )

        assertThat(testContext.captorOne.resultBoolean, equalTo(ValueTrue))
    }


    @Test
    fun `String`() {
        runTest(
            unitOfWork(
                initValue = ValueString("Hamal Rocks"),
                graph = NodesGraph(
                    nodes = listOf(
                        node(1, "Init", listOf(portOutput(20, TypeString))),
                        node(2, "Capture", listOf(portOutput(22, TypeString)))
                    ),
                    connections = listOf(
                        connection(100, 1, 20, 2, 21)
                    ),
                    controls = listOf(
                        ControlTextArea(
                            nextControlIdentifier(),
                            NodeId(2),
                            portInput(21, TypeString),
                            ValueString("default string")
                        )
                    )
                )
            )
        )

        assertThat(testContext.captorOne.resultString, equalTo(ValueString("Hamal Rocks")))
    }


    @Test
    fun `Number`() {
        runTest(
            unitOfWork(
                initValue = ValueNumber(13.37),
                graph = NodesGraph(
                    nodes = listOf(
                        node(1, "Init", listOf(portOutput(20, TypeNumber))),
                        node(2, "Capture", listOf(portOutput(22, TypeNumber)))
                    ),
                    connections = listOf(
                        connection(100, 1, 20, 2, 21)
                    ),
                    controls = listOf(
                        ControlNumberInput(
                            nextControlIdentifier(),
                            NodeId(2),
                            portInput(21, TypeNumber),
                            ValueNumber(0.213)
                        )
                    )
                )
            )
        )

        assertThat(testContext.captorOne.resultNumber, equalTo(ValueNumber(13.37)))
    }


    @Test
    fun `No initial value with selector`() {
        runTest(
            unitOfWork(
                initValue = ValueString("nyanNYANnyanNyan"),
                graph = NodesGraph(
                    nodes = listOf(
                        node(1, "Init", listOf(portOutput(20, TypeString))),
                        node(2, "Test_Invoked", listOf())
                    ),
                    connections = listOf(
                        connection(100, 1, 20, 2, 21)
                    ),
                    controls = listOf(
                        ControlInit(
                            nextControlIdentifier(),
                            NodeId(1),
                            ControlInit.Config(ValueObject.builder().set("selector", "WILL_NOT_FIND_ANYTHING").build())
                        ),
                        ControlTextArea(
                            nextControlIdentifier(),
                            NodeId(2),
                            portInput(21, TypeString),
                            ValueString("default string")
                        )
                    )
                )
            ),
            TestFailConnector { _, statusCode, result ->
                assertThat(statusCode, equalTo(ExecStatusCode(400)))
                assertThat(result.getString("message").stringValue, containsString("No initial value was found"))
            }
        )
        assertThat(testContext.invokedOne.invocations, equalTo(0))
    }


    @Test
    fun `No initial value was set`() {
        runTest(
            unitOfWork(
                initValue = ValueNil,
                graph = NodesGraph(
                    nodes = listOf(
                        node(1, "Init", listOf(portOutput(20, TypeString))),
                        node(2, "Test_Invoked", listOf())
                    ),
                    connections = listOf(
                        connection(100, 1, 20, 2, 21)
                    ),
                    controls = listOf(
                        ControlInit(nextControlIdentifier(), NodeId(1), ControlInit.Config()),
                        ControlTextArea(
                            nextControlIdentifier(),
                            NodeId(2),
                            portInput(21, TypeString),
                            ValueString("default string")
                        )
                    )
                )
            ),
            TestFailConnector { _, statusCode, result ->
                assertThat(statusCode, Matchers.equalTo(ExecStatusCode(400)))
                assertThat(result.getString("message").stringValue, containsString("No initial value was found"))
            }
        )

        assertThat(testContext.invokedOne.invocations, equalTo(0))
    }
}
