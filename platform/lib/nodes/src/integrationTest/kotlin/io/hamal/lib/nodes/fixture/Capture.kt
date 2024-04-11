package io.hamal.lib.nodes.fixture

import io.hamal.lib.nodes.Node
import io.hamal.lib.nodes.NodeType
import io.hamal.lib.nodes.generator.Generator
import io.hamal.lib.typesystem.TypeNew
import io.hamal.lib.typesystem.TypeString


interface GeneratorCapture : Generator {
    override val type: NodeType get() = NodeType("Capture")

    object String : GeneratorCapture {
        override val inputTypes: List<TypeNew> get() = listOf(TypeString)
        override val outputTypes: List<TypeNew> get() = listOf()

        override fun toCode(node: Node): kotlin.String {
            return """
            test = require_plugin('test')
            test.capture1(arg_1)
            return
        """.trimIndent()
        }

    }
}