package io.hamal.lib.nodes.generator

import io.hamal.lib.nodes.Node
import io.hamal.lib.nodes.NodeType
import io.hamal.lib.typesystem.TypeNew
import io.hamal.lib.typesystem.TypeNumber
import io.hamal.lib.typesystem.TypeString

interface GeneratorPrint : Generator {
    override val type: NodeType get() = NodeType("Print")

    object Number : GeneratorPrint {
        override val inputTypes: List<TypeNew> get() = listOf(TypeNumber)
        override val outputTypes: List<TypeNew> get() = listOf()


        override fun toCode(node: Node): kotlin.String {
            return """
            print(arg_1)
            return
        """.trimIndent()
        }

    }

    object String : GeneratorPrint {
        override val inputTypes: List<TypeNew> get() = listOf(TypeString)
        override val outputTypes: List<TypeNew> get() = listOf()

        override fun toCode(node: Node): kotlin.String {
            return """
            print(arg_1)
            return
        """.trimIndent()
        }

    }
}
