package io.hamal.lib.nodes.generator

import io.hamal.lib.nodes.Node
import io.hamal.lib.nodes.NodeType
import io.hamal.lib.nodes.control.Control
import io.hamal.lib.nodes.control.ControlInit
import io.hamal.lib.typesystem.type.Type
import io.hamal.lib.typesystem.type.TypeBoolean
import io.hamal.lib.typesystem.type.TypeNumber
import io.hamal.lib.typesystem.type.TypeString


sealed interface GeneratorInit : Generator {
    override val type: NodeType get() = NodeType("INIT")
    override val inputTypes: List<Type> get() = listOf()

    data object Boolean : GeneratorInit {
        override val outputTypes: List<Type> get() = listOf(TypeBoolean)

        override fun toCode(node: Node, controls: List<Control>): kotlin.String {
            val selector = controls.filterIsInstance<ControlInit>().firstOrNull()?.selector ?: "__nodes__init__"
            if (selector == "NO_VALUE") {
                return "return nil"
            }
            return """return  context.exec.inputs.${selector} or error('No initial value was found')""".trimIndent()
        }
    }

    data object Number : GeneratorInit {
        override val outputTypes: List<Type> get() = listOf(TypeNumber)
        override fun toCode(node: Node, controls: List<Control>): kotlin.String {
            val selector = controls.filterIsInstance<ControlInit>().firstOrNull()?.selector ?: "__nodes__init__"
            if (selector == "NO_VALUE") {
                return "return nil"
            }
            return """return  context.exec.inputs.${selector} or error('No initial value was found')""".trimIndent()
        }
    }

    data object String : GeneratorInit {
        override val outputTypes: List<Type> get() = listOf(TypeString)
        override fun toCode(node: Node, controls: List<Control>): kotlin.String {
            val selector = controls.filterIsInstance<ControlInit>().firstOrNull()?.selector ?: "__nodes__init__"
            if (selector == "NO_VALUE") {
                return "return nil"
            }
            return """return  context.exec.inputs.${selector} or error('No initial value was found')""".trimIndent()
        }
    }


}
