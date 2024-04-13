package io.hamal.lib.nodes.generator

import io.hamal.lib.nodes.Node
import io.hamal.lib.nodes.NodeType
import io.hamal.lib.typesystem.TypeNew


interface Generator {
    val type: NodeType
    val inputTypes: List<TypeNew> // FIXME that should be Parameter (name:type) - so that it can be addressed in lua directly by name
    val outputTypes: List<TypeNew>

    fun toCode(node: Node): String
}


