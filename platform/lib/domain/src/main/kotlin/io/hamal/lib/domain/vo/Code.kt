package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.IntValueObject
import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.domain.ValueObjectString
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.kua.type.StringType

class CodeId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))
}

class CodeValue(override val value: String) : ValueObjectString() {
    constructor(str: StringType) : this(str.value)
}


class CodeVersion(override val value: Int) : IntValueObject() {
    init {
        require(value > 0) { "CodeVersion must be positive" }
    }
}