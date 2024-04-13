package io.hamal.lib.nodes.generator

import io.hamal.lib.nodes.Node
import io.hamal.lib.nodes.NodeType
import io.hamal.lib.typesystem.TypeNew
import io.hamal.lib.typesystem.TypeString


data object GeneratorInit : GeneratorConstant {
    override val type: NodeType get() = NodeType("INIT")
    override val inputTypes: List<TypeNew> get() = listOf()
    override val outputTypes: List<TypeNew> get() = listOf(TypeString)


    override fun toCode(node: Node): kotlin.String {
        return """return  'Hello Hamal' """.trimIndent()
    }
}
