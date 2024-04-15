package io.hamal.lib.nodes.generator

import io.hamal.lib.common.hot.HotBoolean
import io.hamal.lib.common.hot.HotNull
import io.hamal.lib.common.hot.HotNumber
import io.hamal.lib.common.hot.HotString
import io.hamal.lib.nodes.*
import io.hamal.lib.nodes.control.ControlInit
import io.hamal.lib.typesystem.type.TypeBoolean
import io.hamal.lib.typesystem.type.TypeNumber
import io.hamal.lib.typesystem.type.TypeString
import io.hamal.lib.typesystem.value.ValueFalse
import io.hamal.lib.typesystem.value.ValueNumber
import io.hamal.lib.typesystem.value.ValueString
import io.hamal.lib.typesystem.value.ValueTrue
import io.hamal.runner.test.TestFailConnector
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test


internal class InitTest : AbstractIntegrationTest() {

    @Test
    fun `Able to receive value with default selector`() {
        createTestRunner().run(
            unitOfWork(
                initValue = HotString("Hamal Rocks"),
                graph = NodesGraph(
                    nodes = listOf(
                        node(1, "INIT", listOf(portOutput(20, TypeString))),
                        node(2, "CAPTURE", listOf(portOutput(22, TypeString)))
                    ),
                    connections = listOf(
                        connection(100, 1, 20, 2, 21)
                    ),
                    controls = listOf(
                        ControlInit(nextControlId(), NodeId(1)),
                        ControlTextArea(nextControlId(), NodeId(2), portInput(21, TypeString), ValueString("default string"))
                    )
                )
            )
        )

        assertThat(testContext.captorOne.resultString, equalTo(ValueString("Hamal Rocks")))
    }

    @Test
    fun `Boolean`() {
        createTestRunner().run(
            unitOfWork(
                initValue = HotBoolean(true),
                graph = NodesGraph(
                    nodes = listOf(
                        node(1, "INIT", listOf(portOutput(20, TypeBoolean))),
                        node(2, "CAPTURE", listOf(portOutput(22, TypeBoolean)))
                    ),
                    connections = listOf(
                        connection(100, 1, 20, 2, 21)
                    ),
                    controls = listOf(
                        ControlCheckbox(nextControlId(), NodeId(2), portInput(21, TypeBoolean), ValueFalse)
                    )
                )
            )
        )

        assertThat(testContext.captorOne.resultBoolean, equalTo(ValueTrue))
    }


    @Test
    fun `String`() {
        createTestRunner().run(
            unitOfWork(
                initValue = HotString("Hamal Rocks"),
                graph = NodesGraph(
                    nodes = listOf(
                        node(1, "INIT", listOf(portOutput(20, TypeString))),
                        node(2, "CAPTURE", listOf(portOutput(22, TypeString)))
                    ),
                    connections = listOf(
                        connection(100, 1, 20, 2, 21)
                    ),
                    controls = listOf(
                        ControlTextArea(nextControlId(), NodeId(2), portInput(21, TypeString), ValueString("default string"))
                    )
                )
            )
        )

        assertThat(testContext.captorOne.resultString, equalTo(ValueString("Hamal Rocks")))
    }


    @Test
    fun `Number`() {
        createTestRunner().run(
            unitOfWork(
                initValue = HotNumber(13.37),
                graph = NodesGraph(
                    nodes = listOf(
                        node(1, "INIT", listOf(portOutput(20, TypeNumber))),
                        node(2, "CAPTURE", listOf(portOutput(22, TypeNumber)))
                    ),
                    connections = listOf(
                        connection(100, 1, 20, 2, 21)
                    ),
                    controls = listOf(ControlNumberInput(nextControlId(), NodeId(2), portInput(21, TypeNumber), ValueNumber(0.213)))
                )
            )
        )

        assertThat(testContext.captorOne.resultNumber, equalTo(ValueNumber(13.37)))
    }


    @Test
    fun `No initial value with selector`() {
        createTestRunner(
            connector = TestFailConnector { _, execResult ->
                assertThat(execResult.value.stringValue("message"), containsString("No initial value was found"))
            }
        ).run(
            unitOfWork(
                initValue = HotString("nyanNYANnyanNyan"),
                graph = NodesGraph(
                    nodes = listOf(
                        node(1, "INIT", listOf(portOutput(20, TypeString))),
                        node(2, "INVOKED", listOf())
                    ),
                    connections = listOf(
                        connection(100, 1, 20, 2, 21)
                    ),
                    controls = listOf(
                        ControlInit(nextControlId(), NodeId(1), selector = "WILL_NOT_FIND_ANYTHING"),
                        ControlTextArea(nextControlId(), NodeId(2), portInput(21, TypeString), ValueString("default string"))
                    )
                )
            )
        )

        assertThat(testContext.invokedOne.invocations, equalTo(0))
    }


    @Test
    fun `No initial value was set`() {
        createTestRunner(
            connector = TestFailConnector { _, execResult ->
                assertThat(execResult.value.stringValue("message"), containsString("No initial value was found"))
            }
        ).run(
            unitOfWork(
                initValue = HotNull,
                graph = NodesGraph(
                    nodes = listOf(
                        node(1, "INIT", listOf(portOutput(20, TypeString))),
                        node(2, "INVOKED", listOf())
                    ),
                    connections = listOf(
                        connection(100, 1, 20, 2, 21)
                    ),
                    controls = listOf(
                        ControlInit(nextControlId(), NodeId(1)),
                        ControlTextArea(
                            nextControlId(), NodeId(2), portInput(21, TypeString), ValueString("default string")
                        )
                    )
                )
            )
        )

        assertThat(testContext.invokedOne.invocations, equalTo(0))
    }
}
