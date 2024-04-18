package io.hamal.lib.typesystem.value

import io.hamal.lib.typesystem.Field
import io.hamal.lib.typesystem.FieldIdentifier
import io.hamal.lib.typesystem.Property
import io.hamal.lib.typesystem.type.TypeObject

data class ValueObject(
    override val type: TypeObject,
    val properties: List<Property>,
) : Value {

    operator fun <T : Value> get(identifier: FieldIdentifier) = valuesByIdentifier[identifier] as T

    operator fun <T : Value> get(identifier: String) = get<T>(FieldIdentifier(identifier))

    override fun toString() = "${type.identifier}(${properties.joinToString(", ") { it.toString() }})"

    private val valuesByIdentifier = properties.associateBy { it.identifier }.mapValues { it.value.value }
}

fun <T : Value> ValueObject.forType(type: TypeObject, block: () -> T) =
    if (!this.implements(type.fields)) throw NotImplementedError()
    else block()

infix fun ValueObject.implements(interfaceFields: Set<Field>) = this.type.fields.containsAll(interfaceFields)
