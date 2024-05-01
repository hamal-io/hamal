package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.domain.ValueObjectInt
import io.hamal.lib.common.snowflake.SnowflakeId

class CodeId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))
}

class CodeVersion(override val value: Int) : ValueObjectInt() {
    init {
        require(value > 0) { "CodeVersion must be positive" }
    }
}