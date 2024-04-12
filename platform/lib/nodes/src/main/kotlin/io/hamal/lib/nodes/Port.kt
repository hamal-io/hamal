package io.hamal.lib.nodes

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.typesystem.TypeNew

class PortId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))
}


data class PortInput(
    val id: PortId,
    val inputType: TypeNew
)

data class PortOutput(
    val id: PortId,
    val outputType: TypeNew
)