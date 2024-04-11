package io.hamal.lib.nodes.generator

import io.hamal.lib.nodes.*
import io.hamal.lib.nodes.control.ControlConstantString
import io.hamal.lib.nodes.control.ControlInputString
import io.hamal.lib.nodes.fixture.GeneratorCapture
import io.hamal.lib.typesystem.TypeString
import io.hamal.lib.typesystem.value.ValueString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test


internal class ConstantTest : BaseNodesTest() {

    @Test
    fun `ConstantString`() {
        GeneratorRegistry.register(GeneratorConstant)
        GeneratorRegistry.register(GeneratorCapture.String)

        run(
            Graph(
                nodes = listOf(
                    node(
                        id = 1,
                        type = "ConstantString",
                        controls = listOf(ControlConstantString(ValueString("Hamal Rocks"))),
                        outputs = listOf(PortOutput(PortId(20), TypeString))
                    ),
                    node(
                        id = 2,
                        type = "Capture",
                        controls = listOf(
                            ControlInputString(
                                PortInput(PortId(21), TypeString),
                                defaultValue = ValueString("default string")
                            )
                        )
                    )
                ),
                connections = listOf(
                    connection(100, 1, 20, 2, 21)
                )
            )
        )


        assertThat(testCaptor1.result, equalTo(ValueString("Hamal Rocks")))
    }
}
