package io.hamal.lib.nodes.generator

import io.hamal.lib.nodes.*
import io.hamal.lib.nodes.control.ControlConstantDecimal
import io.hamal.lib.nodes.control.ControlConstantString
import io.hamal.lib.nodes.control.ControlInputDecimal
import io.hamal.lib.nodes.control.ControlInputString
import io.hamal.lib.typesystem.TypeDecimal
import io.hamal.lib.typesystem.TypeString
import io.hamal.lib.typesystem.value.ValueDecimal
import io.hamal.lib.typesystem.value.ValueString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test


internal class ConstantTest : BaseNodesTest() {

    @Test
    fun `String`() {
        run(
            Graph(
                nodes = listOf(
                    node(
                        id = 1,
                        type = "Constant",
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
                        ),
                        outputs = listOf(PortOutput(PortId(22), TypeString))
                    )
                ),
                connections = listOf(
                    connection(100, 1, 20, 2, 21)
                )
            )
        )

        assertThat(testCaptor1.resultString, equalTo(ValueString("Hamal Rocks")))
    }


    @Test
    fun `Decimal`() {
        run(
            Graph(
                nodes = listOf(
                    node(
                        id = 1,
                        type = "Constant",
                        controls = listOf(ControlConstantDecimal(ValueDecimal("123456789.987654321"))),
                        outputs = listOf(PortOutput(PortId(20), TypeDecimal))
                    ),
                    node(
                        id = 2,
                        type = "Capture",
                        controls = listOf(
                            ControlInputDecimal(
                                PortInput(PortId(21), TypeDecimal),
                                defaultValue = ValueDecimal("0.213")
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

        assertThat(testCaptor1.resultDecimal, equalTo(ValueDecimal("123456789.987654321")))
    }
}
