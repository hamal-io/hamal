package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.DomainName
import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.snowflake.SnowflakeId

class ExtensionId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))
}


class ExtensionName(override val value: String) : DomainName()


