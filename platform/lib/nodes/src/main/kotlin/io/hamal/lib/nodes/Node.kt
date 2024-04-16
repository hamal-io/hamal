package io.hamal.lib.nodes

import io.hamal.lib.common.domain.ValueObjectHotObject
import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.domain.ValueObjectString
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.snowflake.SnowflakeId


class NodeId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))
}

class NodeTitle(override val value: String) : ValueObjectString()

class NodeType(override val value: String) : ValueObjectString()

class NodeProperties(override val value: HotObject = HotObject.empty) : ValueObjectHotObject()

data class Node(
    val id: NodeId,
    val type: NodeType,
    val title: NodeTitle,
    val position: Position,
    val size: Size,
    val properties: NodeProperties = NodeProperties(),
    val outputs: List<PortOutput> = listOf()
)