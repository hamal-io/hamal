package io.hamal.lib.nodes.generator

import io.hamal.lib.nodes.Node
import io.hamal.lib.nodes.NodeType
import io.hamal.lib.nodes.control.ControlInit
import io.hamal.lib.typesystem.TypeNew
import io.hamal.lib.typesystem.TypeString


sealed interface GeneratorInit : GeneratorConstant {
    override val type: NodeType get() = NodeType("INIT")
    override val inputTypes: List<TypeNew> get() = listOf()


    data object String : GeneratorInit {
        override val outputTypes: List<TypeNew> get() = listOf(TypeString)

        override fun toCode(node: Node): kotlin.String {
            val selector = node.controls.filterIsInstance<ControlInit>().firstOrNull()?.selector ?: "__nodes__init__"
            return """return  context.exec.inputs.${selector} or error('No initial value was found')""".trimIndent()
        }
    }

}
