package io.hamal.lib.nodes

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.domain.ValueObjectString
import io.hamal.lib.common.snowflake.SnowflakeId


class NodeId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))
}

class NodeName(override val value: String) : ValueObjectString()

class NodeTitle(override val value: String) : ValueObjectString()

class NodeType(override val value: String) : ValueObjectString()

data class Node(
    val id: NodeId,
    val type: NodeType,
    val title: NodeTitle,
    val position: Position,
    val size: Size,
    val controls: List<Control> = listOf(),
    val outputs: List<Port> = listOf()
)

