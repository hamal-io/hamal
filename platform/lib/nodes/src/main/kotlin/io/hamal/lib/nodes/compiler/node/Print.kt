package io.hamal.lib.nodes.compiler.node

import io.hamal.lib.common.value.TypeNumber
import io.hamal.lib.common.value.TypeString
import io.hamal.lib.common.value.ValueType
import io.hamal.lib.nodes.Node
import io.hamal.lib.nodes.NodeType
import io.hamal.lib.nodes.NodeType.Companion.NodeType
import io.hamal.lib.nodes.control.Control

sealed interface Print : NodeCompiler {
    override val type: NodeType get() = NodeType("Print")

    object Number : Print {
        override val inputTypes: List<ValueType> get() = listOf(TypeNumber)
        override val outputTypes: List<ValueType> get() = listOf()


        override fun toCode(node: Node, controls: List<Control>): kotlin.String {
            return """
            print(arg_1)
            return
        """.trimIndent()
        }

    }

    object String : Print {
        override val inputTypes: List<ValueType> get() = listOf(TypeString)
        override val outputTypes: List<ValueType> get() = listOf()

        override fun toCode(node: Node, controls: List<Control>): kotlin.String {
            return """
            print(arg_1)
            return
        """.trimIndent()
        }

    }
}
