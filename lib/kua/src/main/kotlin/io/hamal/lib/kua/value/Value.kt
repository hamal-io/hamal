package io.hamal.lib.kua.value

import kotlinx.serialization.Serializable

//@Serializable
interface Value

@Serializable
sealed interface SerializableValue : Value // FIXME

enum class ValueType(
    val value: Int
) {
    Table(5);

    companion object {
        fun ValueType(value: Int): ValueType {
            return ValueType.values().find { it.value == value }
                ?: throw IllegalArgumentException("Invalid value type")
        }
    }
}