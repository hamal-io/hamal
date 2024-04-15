package io.hamal.lib.nodes.generator

import io.hamal.lib.common.hot.HotBoolean
import io.hamal.lib.nodes.*
import io.hamal.lib.typesystem.type.TypeBoolean
import io.hamal.lib.typesystem.value.ValueFalse
import io.hamal.lib.typesystem.value.ValueTrue
import org.hamcrest.CoreMatchers.equalTo
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
                        node(1, "INIT", listOf(PortOutput(PortId(20), TypeBoolean))),
                        node(
                            2, "DECISION", listOf(
                                portOutput(21, TypeBoolean),
                                portOutput(22, TypeBoolean)
                            )
                        ),
                        node(3, "CAPTURE", listOf(portOutput(23, TypeBoolean))),
                        node(4, "CAPTURE", listOf(portOutput(24, TypeBoolean)))
                    ),
                    connections = listOf(
                        connection(100, 1, 20, 2, 30),
                        connection(101, 2, 21, 3, 31, "yes"),
                        connection(102, 2, 22, 4, 32, "no"),
                    ),
                    controls = listOf(
                        ControlCheckbox(nextControlId(), NodeId(2), portInput(30, TypeBoolean), ValueFalse),
                        ControlConnection(nextControlId(), NodeId(3), portInput(31, TypeBoolean)),
                        ControlConnection(nextControlId(), NodeId(4), portInput(32, TypeBoolean))
                    )
                )
            )
        )

        assertThat(testCaptor1.resultBoolean, equalTo(ValueTrue))
    }

}