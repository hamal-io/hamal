//package io.hamal.lib.nodes.compiler.node
//
//import io.hamal.lib.common.value.*
//import io.hamal.lib.nodes.*
//import io.hamal.lib.nodes.NodeIndex.Companion.NodeIndex
//import io.hamal.lib.nodes.PortIndex.Companion.PortIndex
//import org.hamcrest.CoreMatchers.equalTo
//import org.hamcrest.CoreMatchers.nullValue
//import org.hamcrest.MatcherAssert.assertThat
//import org.junit.jupiter.api.Test
//
//internal class DecisionTest : AbstractIntegrationTest() {
//
//    @Test
//    fun `Boolean - happy path`() {
//        runTest(
//            unitOfWork(
//                graph = NodesGraph(
//                    nodes = listOf(
//                        node(1, "Input", listOf(PortOutput(PortIndex(20), TypeBoolean))),
//                        node(2, "Decision", listOf(portOutput(21, TypeBoolean), portOutput(22, TypeBoolean))),
//                        node(
//                            3,
//                            "Capture",
//                            listOf(portOutput(23, TypeBoolean)),
//                            ValueObject.builder().set("capture_fn", ValueString("capture_one")).build()
//                        ),
//                        node(
//                            4,
//                            "Test_Invoked",
//                            listOf(),
//                            ValueObject.builder().set("invoke_fn", ValueString("invoke_one")).build()
//                        ),
//                        node(
//                            5,
//                            "Capture",
//                            listOf(portOutput(25, TypeBoolean)),
//                            ValueObject.builder().set("capture_fn", ValueString("capture_two")).build()
//                        ),
//                        node(
//                            6,
//                            "Test_Invoked",
//                            listOf(),
//                            ValueObject.builder().set("invoke_fn", ValueString("invoke_two")).build()
//                        )
//                    ),
//                    connections = listOf(
//                        connection(100, 1, 20, 2, 30),
//                        connection(101, 2, 21, 3, 31, "yes"),
//                        connection(102, 2, 21, 4, 32, "yes"),
//                        connection(103, 2, 22, 5, 33, "no"),
//                        connection(104, 2, 22, 6, 34, "no"),
//                    ),
//                    controls = listOf(
//                        ControlInputBoolean(nextControlId(), NodeIndex(1), portInput(-1, TypeBoolean), ValueTrue),
//                        ControlInvoke(nextControlId(), NodeIndex(2), portInput(30, TypeBoolean)),
//                        ControlInputBoolean(nextControlId(), NodeIndex(2), portInput(35, TypeBoolean), ValueTrue),
//                        ControlCapture(nextControlId(), NodeIndex(3), portInput(31, TypeBoolean)),
//                        ControlInvoke(nextControlId(), NodeIndex(4), portInput(32, TypeBoolean)),
//                        ControlCapture(nextControlId(), NodeIndex(5), portInput(33, TypeBoolean)),
//                        ControlInvoke(nextControlId(), NodeIndex(6), portInput(34, TypeBoolean))
//                    )
//                )
//            )
//        )
//
//        assertThat(testContext.captorOne.resultBoolean, equalTo(ValueTrue))
//        assertThat(testContext.invokedOne.invocations, equalTo(1))
//        assertThat(testContext.captorTwo.resultBoolean, nullValue())
//        assertThat(testContext.invokedTwo.invocations, equalTo(0))
//    }
//
//    @Test
//    fun `Boolean - sad path`() {
//        runTest(
//            unitOfWork(
//                graph = NodesGraph(
//                    nodes = listOf(
//                        node(1, "Input", listOf(PortOutput(PortIndex(20), TypeBoolean))),
//                        node(2, "Decision", listOf(portOutput(21, TypeBoolean), portOutput(22, TypeBoolean))),
//                        node(
//                            3,
//                            "Capture",
//                            listOf(portOutput(23, TypeBoolean)),
//                            ValueObject.builder().set("capture_fn", ValueString("capture_one")).build()
//                        ),
//                        node(
//                            4,
//                            "Test_Invoked",
//                            listOf(),
//                            ValueObject.builder().set("invoke_fn", ValueString("invoke_one")).build()
//                        ),
//                        node(
//                            5,
//                            "Capture",
//                            listOf(portOutput(25, TypeBoolean)),
//                            ValueObject.builder().set("capture_fn", ValueString("capture_two")).build()
//                        ),
//                        node(
//                            6,
//                            "Test_Invoked",
//                            listOf(),
//                            ValueObject.builder().set("invoke_fn", ValueString("invoke_two")).build()
//                        )
//                    ),
//                    connections = listOf(
//                        connection(100, 1, 20, 2, 30),
//                        connection(101, 2, 21, 3, 31, "yes"),
//                        connection(102, 2, 21, 4, 32, "yes"),
//                        connection(103, 2, 22, 5, 33, "no"),
//                        connection(104, 2, 22, 6, 34, "no"),
//                    ),
//                    controls = listOf(
//                        ControlInputBoolean(nextControlId(), NodeIndex(1), portInput(-1, TypeBoolean), ValueFalse),
//                        ControlInvoke(nextControlId(), NodeIndex(2), portInput(30, TypeBoolean)),
//                        ControlInputBoolean(nextControlId(), NodeIndex(2), portInput(35, TypeBoolean), ValueTrue),
//                        ControlCapture(nextControlId(), NodeIndex(3), portInput(31, TypeBoolean)),
//                        ControlInvoke(nextControlId(), NodeIndex(4), portInput(32, TypeBoolean)),
//                        ControlCapture(nextControlId(), NodeIndex(5), portInput(33, TypeBoolean)),
//                        ControlInvoke(nextControlId(), NodeIndex(6), portInput(34, TypeBoolean))
//                    )
//                )
//            )
//        )
//
//        assertThat(testContext.captorOne.resultBoolean, nullValue())
//        assertThat(testContext.invokedOne.invocations, equalTo(0))
//        assertThat(testContext.captorTwo.resultBoolean, equalTo(ValueFalse))
//        assertThat(testContext.invokedTwo.invocations, equalTo(1))
//    }
//}