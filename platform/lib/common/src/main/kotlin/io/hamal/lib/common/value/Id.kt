package io.hamal.lib.common.value

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.TypeIdentifier.Companion.TypeIdentifier


data object TypeSnowflakeId : Type() {
    override val identifier = TypeIdentifier("Snowflake_Id")
}

data object TypeListSnowflakeId : TypeList() {
    override val identifier = TypeIdentifier("List_Snowflake_Id")
    override val valueType = TypeNumber
}

@JvmInline
value class ValueSnowflakeId(private val value: SnowflakeId) : ValueComparable<ValueSnowflakeId> {
    override val type get() = TypeSnowflakeId
    override fun toString(): String = value.toString()
    override fun compareTo(other: ValueSnowflakeId) = value.compareTo(other.value)
    val snowflakeIdValue: SnowflakeId get() = value
    fun partition() = value.partition()
    fun sequence() = value.sequence()
    fun elapsed() = value.elapsed()
}

abstract class ValueVariableSnowflakeId : ValueVariable.ComparableImpl<ValueSnowflakeId>()