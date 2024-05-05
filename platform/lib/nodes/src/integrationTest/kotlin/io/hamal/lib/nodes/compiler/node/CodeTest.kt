package io.hamal.lib.nodes.compiler.node

import io.hamal.lib.common.value.TypeObject
import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.nodes.AbstractIntegrationTest
import io.hamal.lib.nodes.ControlCode
import io.hamal.lib.nodes.NodeId.Companion.NodeId
import io.hamal.lib.nodes.NodesGraph
import io.hamal.lib.nodes.PortId.Companion.PortId
import io.hamal.lib.nodes.PortOutput
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test


internal class CodeTest : AbstractIntegrationTest() {

    @Test
    fun `Code invoked with object`() {
        createTestRunner().run(
            unitOfWork(
                initValue = ValueObject.builder().set("address", "0x001").build(),
                graph = NodesGraph(
                    nodes = listOf(
                        node(1, "Init", listOf(PortOutput(PortId(20), TypeObject))),
                        node(2, "Code", listOf()),
                    ),
                    connections = listOf(connection(100, 1, 20, 2, 30)),
                    controls = listOf(
                        ControlCode(
                            nextControlIdentifier(), NodeId(2), portInput(30, TypeObject), ValueCode(
                                """
                                    test = require_plugin('test')
                                    test.capture_one(arg_1.address)
                                """.trimIndent()
                            )
                        ),
                    )
                )
            )
        )


        assertThat(testContext.captorOne.resultString, equalTo(ValueString("0x001")))
    }

}