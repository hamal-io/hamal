package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.common.value.ValueVariableString

class RequestId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
}

class RequestClass(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun RequestClass(value: String) = RequestClass(ValueString(value))
    }
}
