package io.hamal.lib.nodes.generator

import io.hamal.lib.nodes.Node
import io.hamal.lib.nodes.NodeType
import io.hamal.lib.nodes.control.Control
import io.hamal.lib.nodes.control.ControlConstantDecimal
import io.hamal.lib.nodes.control.ControlConstantString
import io.hamal.lib.common.value.ValueType
import io.hamal.lib.common.value.TypeDecimal
import io.hamal.lib.common.value.TypeString
import io.hamal.lib.nodes.NodeType.Companion.NodeType

sealed interface GeneratorConstant : Generator {
    override val type: NodeType get() = NodeType("Constant")

    data object String : GeneratorConstant {
        override val inputTypes: List<ValueType> get() = listOf()
        override val outputTypes: List<ValueType> get() = listOf(TypeString)


        override fun toCode(node: Node, controls: List<Control>): kotlin.String {
            check(controls.size == 1)

            val control = controls[0]
            check(control is ControlConstantString)
            return """return  '${control.value.stringValue}'""".trimIndent()
        }
    }

    data object Decimal : GeneratorConstant {
        override val inputTypes: List<ValueType> get() = listOf()
        override val outputTypes: List<ValueType> get() = listOf(TypeDecimal)


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