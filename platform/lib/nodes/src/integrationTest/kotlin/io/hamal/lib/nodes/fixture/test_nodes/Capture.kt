package io.hamal.lib.nodes.fixture.test_nodes

import io.hamal.lib.nodes.Node
import io.hamal.lib.nodes.NodeType
import io.hamal.lib.nodes.control.Control
import io.hamal.lib.nodes.generator.Generator
import io.hamal.lib.typesystem.type.TypeDecimal
import io.hamal.lib.typesystem.type.Type
import io.hamal.lib.typesystem.type.TypeNumber
import io.hamal.lib.typesystem.type.TypeString


interface GeneratorCapture : Generator {
    override val type: NodeType get() = NodeType("CAPTURE")

    object String : GeneratorCapture {
        override val inputTypes: List<Type> get() = listOf(TypeString)
        override val outputTypes: List<Type> get() = listOf(TypeString)

        override fun toCode(node: Node, controls: List<Control>): kotlin.String {
            return """
            test = require_plugin('test')
            test.capture1(arg_1)
            return arg_1
        """.trimIndent()
        }

    }

    object Number : GeneratorCapture {
        override val inputTypes: List<Type> get() = listOf(TypeNumber)
        override val outputTypes: List<Type> get() = listOf(TypeNumber)

        override fun toCode(node: Node, controls: List<Control>): kotlin.String {
            return """
            test = require_plugin('test')
            test.capture1(arg_1)
            return arg_1
        """.trimIndent()
        }

    }

    object Decimal : GeneratorCapture {
        override val inputTypes: List<Type> get() = listOf(TypeDecimal)
        override val outputTypes: List<Type> get() = listOf(TypeDecimal)

        override fun toCode(node: Node, controls: List<Control>): kotlin.String {
            return """
            test = require_plugin('test')
            test.capture1(arg_1)
            return arg_1
        """.trimIndent()
        }

    }
}