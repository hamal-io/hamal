package io.hamal.lib.typesystem

import io.hamal.lib.common.Decimal
import io.hamal.lib.typesystem.value.*
import java.time.LocalDate
import java.time.LocalTime

data class Property(
    val identifier: String,
    val value: Value,
) {
    constructor(field: Field, value: Any) : this(
        field.identifier,
        if (field.isList) {
            ValueList((value as Iterable<*>).map { mapAnyToValue(field, it) })
        } else {
            mapAnyToValue(field, value)
        }
    )

    override fun toString(): String {
        return "$identifier=$value"
    }

    companion object {

        fun mapAnyToValue(field: Field, value: Any?): Value {
            return if (value == null) ValueNil
            else when (field.kind) {
                Kind.Boolean -> valueOf(value as Boolean)
                Kind.Date -> ValueDate(value as LocalDate)
                Kind.Decimal -> ValueDecimal(value as Decimal)

                Kind.List -> TODO()

                Kind.Nil -> ValueNil
                Kind.Number -> ValueNumber(value as Double)

                Kind.Object -> {
                    if (value is ObjectValue && value.type == field.objectKind) {
                        value
                    } else if (value is Map<*, *>) {
                        field.objectKind!!(kwargs = value as Map<String, Any>)
                    } else {
                        throw ClassCastException("cannot cast $value to DTypeValue")
                    }

                }

                Kind.String -> ValueString(value as String)
                Kind.Time -> ValueTime(value as LocalTime)
            }
        }
    }

}