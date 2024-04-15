package io.hamal.lib.typesystem

import io.hamal.lib.common.Decimal
import io.hamal.lib.typesystem.type.*
import io.hamal.lib.typesystem.value.*
import java.time.LocalDate
import java.time.LocalTime

data class Property(
    val identifier: String,
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