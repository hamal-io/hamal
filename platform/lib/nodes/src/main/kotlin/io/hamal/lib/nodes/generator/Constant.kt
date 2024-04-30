package io.hamal.lib.nodes.generator

import io.hamal.lib.nodes.Node
import io.hamal.lib.nodes.NodeType
import io.hamal.lib.nodes.control.Control
import io.hamal.lib.nodes.control.ControlConstantDecimal
import io.hamal.lib.nodes.control.ControlConstantString
import io.hamal.lib.value.Type
import io.hamal.lib.value.TypeDecimal
import io.hamal.lib.value.TypeString

sealed interface GeneratorConstant : Generator {
    override val type: NodeType get() = NodeType("Constant")

    data object String : GeneratorConstant {
        override val inputTypes: List<Type> get() = listOf()
        override val outputTypes: List<Type> get() = listOf(TypeString)


        override fun toCode(node: Node, controls: List<Control>): kotlin.String {
            check(controls.size == 1)

            val control = controls[0]
            check(control is ControlConstantString)
            return """return  '${control.value.stringValue}'""".trimIndent()
        }
    }

    data object Decimal : GeneratorConstant {
        override val inputTypes: List<Type> get() = listOf()
        override val outputTypes: List<Type> get() = listOf(TypeDecimal)


        override fun toCode(node: Node, controls: List<Control>): kotlin.String {
            check(controls.size == 1)

            val control = controls[0]
            check(control is ControlConstantDecimal)
            return """
                local decimal = require('std.decimal')
                return decimal.new('${control.value.value}')
                """.trimIndent()
        }
    }
}