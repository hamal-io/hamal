package io.hamal.lib.nodes.generator

import io.hamal.lib.nodes.Node
import io.hamal.lib.nodes.NodeType
import io.hamal.lib.nodes.control.Control
import io.hamal.lib.nodes.control.ControlInit
import io.hamal.lib.common.value.Type
import io.hamal.lib.common.value.TypeBoolean
import io.hamal.lib.common.value.TypeNumber
import io.hamal.lib.common.value.TypeString


sealed interface GeneratorInit : Generator {
    override val type: NodeType get() = NodeType("Init")
    override val inputTypes: List<Type> get() = listOf()

    data object Boolean : GeneratorInit {
        override val outputTypes: List<Type> get() = listOf(TypeBoolean)

        override fun toCode(node: Node, controls: List<Control>): kotlin.String {
            val selector = controls.filterIsInstance<ControlInit>().firstOrNull()?.selector ?: "__nodes__init__"
            if (selector == "No_Value") {
                return "return nil"
            }
            return """
                initial_value = context.exec.inputs.${selector}
                if initial_value == nil then
                    error('No initial value was found')
                end
                return initial_value 
            """.trimIndent()
        }
    }

    data object Number : GeneratorInit {
        override val outputTypes: List<Type> get() = listOf(TypeNumber)
        override fun toCode(node: Node, controls: List<Control>): kotlin.String {
            val selector = controls.filterIsInstance<ControlInit>().firstOrNull()?.selector ?: "__nodes__init__"
            if (selector == "No_Value") {
                return "return nil"
            }
            return """
                initial_value = context.exec.inputs.${selector}
                if initial_value == nil then
                    error('No initial value was found')
                end
                return initial_value 
            """.trimIndent()
        }
    }

    data object String : GeneratorInit {
        override val outputTypes: List<Type> get() = listOf(TypeString)
        override fun toCode(node: Node, controls: List<Control>): kotlin.String {
            val selector = controls.filterIsInstance<ControlInit>().firstOrNull()?.selector ?: "__nodes__init__"
            if (selector == "No_Value") {
                return "return nil"
            }
            return """
                initial_value = context.exec.inputs.${selector}
                if initial_value == nil then
                    error('No initial value was found')
                end
                return initial_value 
            """.trimIndent()
        }
    }


}
