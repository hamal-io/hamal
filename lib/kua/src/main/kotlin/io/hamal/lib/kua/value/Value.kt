package io.hamal.lib.kua.value

import kotlinx.serialization.Serializable

interface Value

@Serializable
sealed interface SerializableValue : Value

enum class ValueType(
    val value: Int
) {
    Nil(0),
    Boolean(1),
    Number(3),
    String(4),
    Table(5);

    companion object {
        fun ValueType(value: Int): ValueType {
            return ValueType.values().find { it.value == value }
                ?: throw IllegalArgumentException("Invalid value type")
        }
    }
}