package io.hamal.lib.domain.vo

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.*
import io.hamal.lib.domain._enum.RequestStatuses

class RequestId(override val value: ValueSnowflakeId) : ValueVariableSnowflakeId() {
    companion object {
        fun RequestId(value: SnowflakeId) = RequestId(ValueSnowflakeId(value))
        fun RequestId(value: Int) = RequestId(ValueSnowflakeId(SnowflakeId(value.toLong())))
        fun RequestId(value: String) = RequestId(ValueSnowflakeId(SnowflakeId(value.toLong(16))))
    }
}

class RequestClass(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun RequestClass(value: String) = RequestClass(ValueString(value))
    }
}

class RequestStatus(override val value: ValueEnum) : ValueVariableEnum<RequestStatuses>(RequestStatuses::class) {
    companion object {
        fun RequestStatus(value: Enum<RequestStatuses>) = RequestStatus(ValueEnum(value.name))
    }
}