package io.hamal.lib.common.value

import io.hamal.lib.common.Decimal
import io.hamal.lib.common.value.FieldIdentifier.Companion.FieldIdentifier
import java.time.LocalDate
import java.time.LocalTime


class FieldIdentifier(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun FieldIdentifier(value: String) = FieldIdentifier(ValueString(value))
    }
}

data class Field(
    val type: Type,
    val identifier: FieldIdentifier
) {
    constructor(type: Type, identifier: String) : this(type, FieldIdentifier(identifier))
}

class TypeIdentifier(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun TypeIdentifier(value: String) = TypeIdentifier(ValueString(value))
    }
}

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

sealed class TypePrimitive : Type()

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
            else when (val fieldType = field.type) {
                is TypeArray -> {
                    if (value is Iterable<*>) {
                        ValueArray(field.type, value.map { mapTypeToValue(fieldType.valueType, it) })
                    } else {
                        TODO()
                    }
                }

                is TypeBoolean -> ValueBoolean(value as Boolean)
                is TypeCode -> TODO()
                is TypeDate -> ValueDate(value as LocalDate)
                is TypeDateTime -> TODO()
                is TypeDecimal -> ValueDecimal(value as Decimal)
                is TypeError -> TODO()
                is TypeInstant -> TODO()

                is TypeNil -> ValueNil
                is TypeNumber -> ValueNumber(value as Double)
                is TypeObject -> {
                    if (value is ValueObject && value.type == fieldType) {
                        value
                    } else if (value is Map<*, *>) {
                        fieldType(kwargs = value as Map<String, Any>)
                    } else {
                        throw ClassCastException("cannot cast $value to DTypeValue")
                    }
                }

                is TypeSnowflakeId -> TODO()
                is TypeString -> ValueString(value as String)
                is TypeTime -> ValueTime(value as LocalTime)
            }
        }
    }

}

