package io.hamal.lib.nodes.generator

import io.hamal.lib.nodes.Node
import io.hamal.lib.nodes.NodeType
import io.hamal.lib.nodes.control.ControlConstant
import io.hamal.lib.nodes.control.ControlConstantString
import io.hamal.lib.typesystem.TypeNew
import io.hamal.lib.typesystem.TypeString

object GeneratorConstant : Generator {
    override val type: NodeType get() = NodeType("ConstantString")

    override val inputTypes: List<TypeNew> get() = listOf()
    override val outputTypes: List<TypeNew> get() = listOf(TypeString)

//    Notoverride val fields: List<Field>
//        get() = listOf(
//            Field(Field.Kind.String, "arg1")
//        )


    override fun toCode(node: Node): String {
        val controls = node.controls
        check(controls.size == 1)

        val control = controls[0]
        check(control is ControlConstant)

        if (control is ControlConstantString) {
            return """
            return  '${control.value.stringValue}'
        """.trimIndent()
        } else {
            TODO()
        }
    }
}