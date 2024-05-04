package io.hamal.lib.common.value

import io.hamal.lib.common.snowflake.SnowflakeId


val TypeSnowflakeId = ValueType("Snowflake_Id")

@JvmInline
value class ValueSnowflakeId(private val value: SnowflakeId) : ValueComparable<ValueSnowflakeId> {
    constructor(value: String) : this(SnowflakeId(value))

    override val type get() = TypeSnowflakeId
    override fun toString(): String = value.toString()
    override fun compareTo(other: ValueSnowflakeId) = value.compareTo(other.value)
    val snowflakeIdValue: SnowflakeId get() = value
    val stringValue: String get() = value.toString()
    fun partition() = value.partition()
    fun sequence() = value.sequence()
    fun elapsed() = value.elapsed()
}

abstract class ValueVariableSnowflakeId : ValueVariable.ComparableImpl<ValueSnowflakeId>() {
    val longValue: Long get() = value.snowflakeIdValue.toLong()
    val stringValue: String get() = value.toString()
    fun partition() = value.partition()
    fun sequence() = value.sequence()
    fun elapsed() = value.elapsed()
}