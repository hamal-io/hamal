package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.ValueObjecHotObject
import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.domain.ValueObjectString
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.snowflake.SnowflakeId

class EndpointId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))
}

class EndpointName(override val value: String) : ValueObjectString()

class EndpointHeaders(override val value: HotObject = HotObject.empty) : ValueObjecHotObject()

class EndpointParameters(override val value: HotObject = HotObject.empty) : ValueObjecHotObject()

class EndpointContent(override val value: HotObject = HotObject.empty) : ValueObjecHotObject()
