package io.hamal.lib.nodes.generator

import io.hamal.lib.nodes.Node
import io.hamal.lib.nodes.NodeType
import io.hamal.lib.nodes.control.ControlConstantString
import io.hamal.lib.typesystem.TypeNew
import io.hamal.lib.typesystem.TypeString

sealed interface GeneratorConstant : Generator {
    override val type: NodeType get() = NodeType("Constant")

    data object String : GeneratorConstant {
        override val inputTypes: List<TypeNew> get() = listOf()
        override val outputTypes: List<TypeNew> get() = listOf(TypeString)


        override fun toCode(node: Node): kotlin.String {
            val controls = node.controls
            check(controls.size == 1)

            val control = controls[0]
            check(control is ControlConstantString)
            return """return  '${control.value.stringValue}'""".trimIndent()
        }
    }
}