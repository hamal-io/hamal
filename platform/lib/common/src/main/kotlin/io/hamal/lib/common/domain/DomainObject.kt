package io.hamal.lib.common.domain

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.common.value.ValueSnowflakeId
import io.hamal.lib.common.value.ValueVariableSnowflakeId
import java.time.Instant


interface DomainObject<ID : ValueVariableSnowflakeId> {
    val id: ID
    val partition get() = id.partition()
    val createdAt get() = CreatedAt(Instant.ofEpochMilli(id.value.elapsed().value + 1698451200000L))
    val updatedAt: UpdatedAt
}

data class CmdId(override val value: ValueSnowflakeId) : ValueVariableSnowflakeId() {
    companion object {
        fun CmdId(value: SnowflakeId) = CmdId(ValueSnowflakeId(value))
        fun CmdId(value: Int) = CmdId(ValueSnowflakeId(SnowflakeId(value.toLong())))
        fun CmdId(value: String) = CmdId(ValueSnowflakeId(SnowflakeId(value.toLong(16))))
    }
}

class CreatedAt(override val value: Instant) : ValueObjectInstant() {
    companion object {
        @JvmStatic
        fun now(): CreatedAt = CreatedAt(TimeUtils.now())
    }
}

class UpdatedAt(override val value: Instant) : ValueObjectInstant() {
    companion object {
        @JvmStatic
        fun now(): UpdatedAt = UpdatedAt(TimeUtils.now())
    }
}