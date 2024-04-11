package io.hamal.lib.nodes

import io.hamal.lib.nodes.control.ControlConstantString
import io.hamal.lib.nodes.control.ControlId
import io.hamal.lib.nodes.control.ControlInputString
import io.hamal.lib.nodes.fixture.GeneratorCapture
import io.hamal.lib.nodes.generator.GeneratorConstant
import io.hamal.lib.nodes.generator.GeneratorRegistry
import io.hamal.lib.typesystem.TypeString
import io.hamal.lib.typesystem.value.ValueString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

internal class CaptureTest : BaseNodesTest() {

    @Test
    fun `Captures String`() {
        GeneratorRegistry.register(GeneratorConstant)
        GeneratorRegistry.register(GeneratorCapture.String)

        run(
            Graph(
                nodes = listOf(
                    node(
                        id = 1,
                        type = "ConstantString",
                        controls = listOf(ControlConstantString(ControlId(23), ValueString("Hamal Rocks"))),
                        outputs = listOf(PortOutput(PortId(222), TypeString))
                    ),
                    node(
                        id = 2,
                        type = "Capture",
                        controls = listOf(
                            ControlInputString(
                                ControlId(24),
                                PortInput(PortId(333), TypeString),
                                defaultValue = ValueString("default string")
                            )
                        )
                    )
                ),
                connections = listOf(
                    connection(100, 1, 222, 2, 333)
                )
            )
        )


        assertThat(testCaptor1.result, equalTo(ValueString("Hamal Rocks")))
    }
}