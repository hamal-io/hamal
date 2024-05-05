package io.hamal.lib.common.value

import io.hamal.lib.common.value.ValueVariable.BaseImpl
import kotlin.reflect.KClass

val TypeEnum = ValueType("Enum")

data class ValueEnum(val value: String) : ValueSerializable {
    constructor(value: Enum<*>) : this(value.name)

    override val type get() = TypeEnum

    inline fun <reified T : Enum<T>> enumValue(): T = enumValueOf<T>(value)
    val stringValue: String get() = value
    override fun toString() = stringValue

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ValueEnum

        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}


abstract class ValueVariableEnum<T : Enum<T>>(private val enumClass: KClass<T>) : BaseImpl<ValueEnum>() {
    val enumValue get() = checkNotNull(enumClass.java.enumConstants.find { it.name == value.value })
    val stringValue: String get() = value.stringValue

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass == enumClass.java) {
            require(other is Enum<*>)
            return value.value == other.name
        }

        if (javaClass != other?.javaClass) return false
        other as BaseImpl<*>
        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

}