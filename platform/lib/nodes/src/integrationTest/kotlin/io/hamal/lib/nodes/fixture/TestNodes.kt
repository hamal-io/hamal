package io.hamal.lib.nodes.fixture

import io.hamal.lib.nodes.*
import io.hamal.lib.nodes.control.ControlConstantString
import io.hamal.lib.nodes.control.ControlInputString
import io.hamal.lib.nodes.generator.Generator
import io.hamal.lib.nodes.generator.GeneratorConstant
import io.hamal.lib.nodes.generator.GeneratorRegistry
import io.hamal.lib.typesystem.TypeAny
import io.hamal.lib.typesystem.TypeNew
import io.hamal.lib.typesystem.TypeString
import io.hamal.lib.typesystem.value.ValueString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test


object GeneratorInvoked : Generator {
    override val type: NodeType get() = NodeType("Invoked")
    override val inputTypes: List<TypeNew> get() = listOf(TypeAny)
    override val outputTypes: List<TypeNew> get() = listOf()

    override fun toCode(node: Node): String {
        return """
            test = require_plugin('test')
            test.invoked()
            return 
        """.trimIndent()
    }
}

internal class InvokedTest : BaseNodesTest() {

    @Test
    fun `Nodes invokes another node`() {
        GeneratorRegistry.register(GeneratorConstant)
        GeneratorRegistry.register(GeneratorInvoked)

        run(
            Graph(
                nodes = listOf(
                    node(
                        id = 1,
                        type = "ConstantString",
                        controls = listOf(ControlConstantString(ValueString("Hamal Rocks"))),
                        outputs = listOf(PortOutput(PortId(20), TypeString))
                    ),
                    node(id = 2, type = "Invoked", listOf(ControlInputString(PortInput(PortId(21), TypeString), ValueString("default"))))
                ),
                connections = listOf(
                    connection(100, 1, 20, 2, 21)
                )
            )
        )

        assertThat(testInvoked.invocations, equalTo(1))
    }

    @Test
    fun `Nodes invokes multiple nodes`() {
        GeneratorRegistry.register(GeneratorConstant)
        GeneratorRegistry.register(GeneratorInvoked)

        run(
            Graph(
                nodes = listOf(
                    node(
                        id = 1,
                        type = "ConstantString",
                        controls = listOf(ControlConstantString(ValueString("Hamal Rocks"))),
                        outputs = listOf(PortOutput(PortId(20), TypeString))
                    ),
                    node(id = 2, type = "Invoked", listOf(ControlInputString(PortInput(PortId(21), TypeString), ValueString("default")))),
                    node(id = 3, type = "Invoked", listOf(ControlInputString(PortInput(PortId(22), TypeString), ValueString("default"))))
                ),
                connections = listOf(
                    connection(100, 1, 20, 2, 21),
                    connection(100, 1, 20, 3, 22),
                )
            )
        )

        assertThat(testInvoked.invocations, equalTo(2))
    }
}

interface GeneratorCapture : Generator {
    override val type: NodeType get() = NodeType("Capture")

    object String : GeneratorCapture {
        override val inputTypes: List<TypeNew> get() = listOf(TypeString)
        override val outputTypes: List<TypeNew> get() = listOf(TypeString)

        override fun toCode(node: Node): kotlin.String {
            return """
            test = require_plugin('test')
            test.capture1(arg_1)
            return arg_1
        """.trimIndent()
        }

    }
}

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
