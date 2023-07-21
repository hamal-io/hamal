package io.hamal.lib.kua.table

import io.hamal.lib.kua.value.NumberValue
import io.hamal.lib.kua.value.StringValue
import io.hamal.lib.kua.value.Value

interface TableMap : Value {
    operator fun set(key: String, value: String): TableLength
    operator fun set(key: String, value: StringValue) = set(key, value.value)
    operator fun set(key: StringValue, value: StringValue) = set(key.value, value.value)

    operator fun set(key: String, value: Double): TableLength
    operator fun set(key: String, value: NumberValue) = set(key, value.value)
    operator fun set(key: StringValue, value: NumberValue) = set(key.value, value.value)
}
