package io.hamal.lib.nodes

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.domain.ValueObjectString
import io.hamal.lib.common.snowflake.SnowflakeId


class NodeId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))
}

class NodeName(override val value: String) : ValueObjectString()

class NodeLabel(override val value: String) : ValueObjectString()

class NodeType(override val value: String) : ValueObjectString()

interface Node {
    val id: NodeId
}


interface NodeWithInputs : Node {
    val inputPortIds: List<PortId>
}

interface NodeWithOutputs : Node {
    val outputPortIds: List<PortId>
}