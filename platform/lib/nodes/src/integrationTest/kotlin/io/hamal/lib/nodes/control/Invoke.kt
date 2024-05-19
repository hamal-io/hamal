//package io.hamal.lib.nodes.control
//
//import io.hamal.lib.common.value.TypeString
//import io.hamal.lib.common.value.ValueString
//import io.hamal.lib.nodes.*
//import io.hamal.lib.nodes.NodeIndex.Companion.NodeIndex
//import io.hamal.lib.nodes.PortIndex.Companion.PortIndex
//import org.hamcrest.CoreMatchers.equalTo
//import org.hamcrest.MatcherAssert.assertThat
//import org.junit.jupiter.api.Disabled
//import org.junit.jupiter.api.Test
//
//
//internal object ControlInvokeTest : AbstractIntegrationTest() {
//
//    @Test
//    @Disabled
//    fun `A node can be invoked without exchanging any data`() {
//        // FIXME what is this?
//        runTest(
//            unitOfWork(
////                initValue = ValueString("This value will not passed into capture node"),
//                graph = NodesGraph(
//                    nodes = listOf(
//                        node(1, "Init", listOf(PortOutput(PortIndex(20), TypeString))),
//                        node(2, "Capture", listOf(PortOutput(PortIndex(22), TypeString)))
//                    ),
//                    connections = listOf(
//                        connection(100, 1, 20, 2, 21)
//                    ),
//                    controls = listOf(
//                        ControlInvoke(nextControlId(), NodeIndex(2), portInput(21, TypeString)),
//                        ControlInputString(
//                            nextControlId(),
//                            NodeIndex(2),
//                            portInput(22, TypeString),
//                            ValueString("default capture string")
//                        ),
//                    )
//                )
//            )
//        )
//
//        assertThat(testContext.captorOne.resultString, equalTo(ValueString("default capture string")))
//    }
//
//}