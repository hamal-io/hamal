package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.domain.ValueObjectString
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.base.MapValueObject
import io.hamal.lib.kua.type.MapType

class EndpointId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))
}

class EndpointName(override val value: String) : ValueObjectString()

class EndpointHeaders(override val value: MapType = MapType()) : MapValueObject()

class EndpointParameters(override val value: MapType = MapType()) : MapValueObject()

class EndpointContent(override val value: MapType = MapType()) : MapValueObject()
