package io.hamal.lib.common.domain

import io.hamal.lib.common.snowflake.SnowflakeId

abstract class ValueObjectId : ValueObject.ComparableImpl<SnowflakeId>() {
    fun partition() = value.partition()
    fun sequence() = value.sequence()
    fun elapsed() = value.elapsed()
    override fun toString(): String {
        return "${this::class.simpleName}(${value.value})"
    }
}