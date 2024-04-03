package io.hamal.lib.nodes

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.domain.ValueObjectString
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.typesystem.TypeNew

class PortId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))
}

class PortName(override val value: String) : ValueObjectString()


data class Port(
    val id: PortId,
    val name: PortName,
    val type: Type,
    val valueType: TypeNew
) {
    enum class Type {
        Input,
        Output
    }
}