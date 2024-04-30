package io.hamal.lib.value

import io.hamal.lib.common.Decimal
import io.hamal.lib.common.domain.ValueObjectString
import java.time.LocalDate
import java.time.LocalTime

class TypeIdentifier(override val value: String) : ValueObjectString()

sealed class Type {
    abstract val identifier: TypeIdentifier

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Type
        return identifier == other.identifier
    }

    override fun hashCode(): Int {
        return identifier.hashCode()
    }
}

sealed class TypeList : Type() {
    abstract val valueType: Type
    operator fun invoke(vararg any: Any): ValueList {
        return ValueList(this, any.map { v -> Property.mapTypeToValue(valueType, v) })
    }
}


data class Property(
    val identifier: FieldIdentifier,
    val value: Value,
) {

    override fun toString(): String {
        return "$identifier=$value"
    }

    companion object {

        fun Property(field: Field, value: Any): Property {
            return Property(field.identifier, mapAnyToValue(field, value))
        }

        fun mapTypeToValue(type: Type, value: Any?): Value {
            return when (value) {
                is Value -> value
                else -> TODO()
            }
        }

        private fun mapAnyToValue(field: Field, value: Any?): Value {
            return if (value == null) ValueNil
            else when (field.type) {
                is TypeBoolean -> valueOf(value as Boolean)
                is TypeCode -> TODO()
                is TypeDate -> ValueDate(value as LocalDate)
                is TypeDateTime -> TODO()
                is TypeDecimal -> ValueDecimal(value as Decimal)
                is TypeList -> {
                    if (value is Iterable<*>) {
                        ValueList(field.type, value.map { mapTypeToValue(field.valueType!!, it) })
                    } else {
                        TODO()
                    }
                }

                is TypeNil -> ValueNil
                is TypeNumber -> ValueNumber(value as Double)
                is TypeObject -> {
                    if (value is ValueObject && value.type == field.valueType) {
                        value
                    } else if (value is Map<*, *>) {
                        field.valueType!!(kwargs = value as Map<String, Any>)
                    } else {
                        throw ClassCastException("cannot cast $value to DTypeValue")
                    }
                }

                is TypeString -> ValueString(value as String)
                is TypeTime -> ValueTime(value as LocalTime)
            }
        }
    }

}

