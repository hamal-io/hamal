package io.hamal.lib.nodes.generator

import io.hamal.lib.common.value.Type
import io.hamal.lib.common.value.TypeNumber
import io.hamal.lib.common.value.TypeString
import io.hamal.lib.nodes.Node
import io.hamal.lib.nodes.NodeType
import io.hamal.lib.nodes.NodeType.Companion.NodeType
import io.hamal.lib.nodes.control.Control

interface GeneratorPrint : Generator {
    override val type: NodeType get() = NodeType("Print")

    object Number : GeneratorPrint {
        override val inputTypes: List<Type> get() = listOf(TypeNumber)
        override val outputTypes: List<Type> get() = listOf()


        override fun toCode(node: Node, controls: List<Control>): kotlin.String {
            return """
            print(arg_1)
            return
        """.trimIndent()
        }

    }

    object String : GeneratorPrint {
        override val inputTypes: List<Type> get() = listOf(TypeString)
        override val outputTypes: List<Type> get() = listOf()

        override fun toCode(node: Node, controls: List<Control>): kotlin.String {
            return """
            print(arg_1)
            return
        """.trimIndent()
        }

    }
}
