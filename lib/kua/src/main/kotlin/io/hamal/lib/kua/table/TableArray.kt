package io.hamal.lib.kua.table

import io.hamal.lib.kua.value.BooleanValue
import io.hamal.lib.kua.value.NumberValue
import io.hamal.lib.kua.value.StringValue

interface TableArrayProxyValue : BaseTableProxyValue {
    fun append(value: Boolean): TableLength
    fun append(value: BooleanValue) = append(value.value)

    fun append(value: Double): TableLength
    fun append(value: Int) = append(value.toDouble())
    fun append(value: Long) = append(value.toDouble())
    fun append(value: Float) = append(value.toDouble())
    fun append(value: NumberValue) = append(value.value)

    fun append(value: String): TableLength
    fun append(value: StringValue) = append(value.value)
}