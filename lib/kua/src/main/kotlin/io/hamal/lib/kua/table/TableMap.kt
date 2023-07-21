package io.hamal.lib.kua.table

import io.hamal.lib.kua.value.*

interface TableMap : Value {
    fun unset(key: String): TableLength
    fun unset(key: StringValue) = unset(key.value)
    operator fun set(key: String, value: NilValue) = unset(key)
    operator fun set(key: StringValue, value: NilValue) = unset(key.value)

    operator fun set(key: String, value: Boolean): TableLength
    operator fun set(key: String, value: BooleanValue) = set(key, value.value)
    operator fun set(key: StringValue, value: BooleanValue) = set(key.value, value.value)

    operator fun set(key: String, value: Double): TableLength
    operator fun set(key: String, value: NumberValue) = set(key, value.value)
    operator fun set(key: StringValue, value: NumberValue) = set(key.value, value.value)

    operator fun set(key: String, value: String): TableLength
    operator fun set(key: String, value: StringValue) = set(key, value.value)
    operator fun set(key: StringValue, value: StringValue) = set(key.value, value.value)
}
