package io.hamal.lib.nodes.generator

import io.hamal.lib.nodes.Node
import io.hamal.lib.nodes.NodeType
import io.hamal.lib.typesystem.TypeNew


interface Generator {
    val type: NodeType
    val inputTypes: List<TypeNew>
    val outputTypes: List<TypeNew>

    fun toCode(node: Node): String
}


