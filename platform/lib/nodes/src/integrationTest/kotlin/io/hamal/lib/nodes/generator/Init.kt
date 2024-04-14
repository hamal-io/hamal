package io.hamal.lib.nodes.generator

import io.hamal.lib.common.hot.HotNull
import io.hamal.lib.common.hot.HotNumber
import io.hamal.lib.common.hot.HotString
import io.hamal.lib.nodes.*
import io.hamal.lib.nodes.control.ControlInit
import io.hamal.lib.nodes.control.ControlInputNumber
import io.hamal.lib.nodes.control.ControlInputString
import io.hamal.lib.typesystem.TypeDecimal
import io.hamal.lib.typesystem.TypeNumber
import io.hamal.lib.typesystem.TypeString
import io.hamal.lib.typesystem.value.ValueNumber
import io.hamal.lib.typesystem.value.ValueString
import io.hamal.runner.test.TestFailConnector
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test


internal class InitTest : AbstractNodesTest() {

    @Test
    fun `Able to receive value with default selector`() {
        createTestRunner().run(
            unitOfWork(
                initValue = HotString("Hamal Rocks"),
                graph = NodesGraph(
                    nodes = listOf(
                        node(
                            id = 1,
                            type = "INIT",
                            controls = listOf(ControlInit()),
                            outputs = listOf(PortOutput(PortId(20), TypeString))
                        ),
                        node(
                            id = 2,
                            type = "CAPTURE",
                            controls = listOf(
                                ControlInputString(
                                    PortInput(PortId(21), TypeString),
                                    defaultValue = ValueString("default string")
                                )
                            ),
                            outputs = listOf(PortOutput(PortId(22), TypeString))
                        )
                    ),
                    connections = listOf(
                        connection(100, 1, 20, 2, 21)
                    )
                )
            )
        )

        assertThat(testCaptor1.resultString, equalTo(ValueString("Hamal Rocks")))
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
                        node(
                            id = 1,
                            type = "INIT",
                            controls = listOf(ControlInit(selector = "WILL_NOT_FIND_ANYTHING")),
                            outputs = listOf(PortOutput(PortId(20), TypeString))
                        ),
                        node(
                            id = 2,
                            type = "INVOKED",
                            controls = listOf(
                                ControlInputString(
                                    PortInput(PortId(21), TypeString),
                                    defaultValue = ValueString("default string")
                                )
                            ),
                            outputs = listOf()
                        )
                    ),
                    connections = listOf(
                        connection(100, 1, 20, 2, 21)
                    )
                )
            )
        )

        assertThat(testInvoked.invocations, equalTo(0))
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
                        node(
                            id = 1,
                            type = "INIT",
                            controls = listOf(ControlInit()),
                            outputs = listOf(PortOutput(PortId(20), TypeString))
                        ),
                        node(
                            id = 2,
                            type = "INVOKED",
                            controls = listOf(
                                ControlInputString(
                                    PortInput(PortId(21), TypeString),
                                    defaultValue = ValueString("default string")
                                )
                            ),
                            outputs = listOf()
                        )
                    ),
                    connections = listOf(
                        connection(100, 1, 20, 2, 21)
                    )
                )
            )
        )

        assertThat(testInvoked.invocations, equalTo(0))
    }

    @Test
    fun `String`() {
        createTestRunner().run(
            unitOfWork(
                initValue = HotString("Hamal Rocks"),
                graph = NodesGraph(
                    nodes = listOf(
                        node(
                            id = 1,
                            type = "INIT",
                            controls = listOf(),
                            outputs = listOf(PortOutput(PortId(20), TypeString))
                        ),
                        node(
                            id = 2,
                            type = "CAPTURE",
                            controls = listOf(
                                ControlInputString(
                                    PortInput(PortId(21), TypeString),
                                    defaultValue = ValueString("default string")
                                )
                            ),
                            outputs = listOf(PortOutput(PortId(22), TypeString))
                        )
                    ),
                    connections = listOf(
                        connection(100, 1, 20, 2, 21)
                    )
                )
            )
        )

        assertThat(testCaptor1.resultString, equalTo(ValueString("Hamal Rocks")))
    }


    @Test
    fun `Number`() {
        createTestRunner().run(
            unitOfWork(
                initValue = HotNumber(13.37),
                graph = NodesGraph(
                    nodes = listOf(
                        node(
                            id = 1,
                            type = "INIT",
                            controls = listOf(),
                            outputs = listOf(PortOutput(PortId(20), TypeNumber))
                        ),
                        node(
                            id = 2,
                            type = "CAPTURE",
                            controls = listOf(
                                ControlInputNumber(
                                    PortInput(PortId(21), TypeNumber),
                                    defaultValue = ValueNumber(0.213)
                                )
                            ),
                            outputs = listOf(PortOutput(PortId(22), TypeDecimal))
                        )
                    ),
                    connections = listOf(
                        connection(100, 1, 20, 2, 21)
                    )
                )
            )
        )

        assertThat(testCaptor1.resultNumber, equalTo(ValueNumber(13.37)))
    }
}
