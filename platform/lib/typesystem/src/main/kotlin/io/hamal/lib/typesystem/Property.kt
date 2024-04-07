package io.hamal.lib.typesystem

import io.hamal.lib.common.Decimal
import io.hamal.lib.typesystem.Field.Kind
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

        fun of(field: Field, value: Any): Property {
            return Property(field.identifier, mapAnyToValue(field, value))
        }

        fun mapTypeToValue(type: Type, value: Any?): Value {
            return when (value) {
                is Value -> value
                else -> TODO()
            }
        }

        fun mapAnyToValue(field: Field, value: Any?): Value {
            return if (value == null) ValueNil
            else when (field.kind) {
                Kind.Any -> TODO()

                Kind.Boolean -> valueOf(value as Boolean)
                Kind.Date -> ValueDate(value as LocalDate)
                Kind.Decimal -> ValueDecimal(value as Decimal)

                Kind.List -> {
                    if (value is Iterable<*>) {
                        ValueList(
                            value.map { mapTypeToValue(field.valueType!!, it) },
                            field.valueType!!
                        )
                    } else {
                        TODO()
                    }
                }

                Kind.Nil -> ValueNil
                Kind.Number -> ValueNumber(value as Double)

                Kind.Object -> {
                    if (value is ValueObject && value.type == field.valueType) {
                        value
                    } else if (value is Map<*, *>) {
                        field.valueType!!(kwargs = value as Map<String, Any>)
                    } else {
                        throw ClassCastException("cannot cast $value to DTypeValue")
                    }
                }

                Kind.OneOf -> TODO()
                Kind.String -> ValueString(value as String)
                Kind.Time -> ValueTime(value as LocalTime)
            }
        }
    }

}