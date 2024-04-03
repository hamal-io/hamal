package io.hamal.lib.typesystem.value

import io.hamal.lib.typesystem.Field
import io.hamal.lib.typesystem.Field.Kind
import io.hamal.lib.typesystem.Property
import io.hamal.lib.typesystem.Type

data class ValueObject(
    val type: Type,
    val properties: List<Property>,
) : Value {
    override val kind get() = Kind.Object

    operator fun <T : Value> get(identifier: String) = valuesByIdentifier[identifier] as T

    override fun toString() = "${type.identifier}(${properties.joinToString(", ") { it.toString() }})"

    private val valuesByIdentifier = properties.associateBy { it.identifier }.mapValues { it.value.value }
}

fun <T : Value> ValueObject.forType(type: Type, block: () -> T) =
    if (!this.implements(type.fields)) throw NotImplementedError()
    else block()

infix fun ValueObject.implements(interfaceFields: Set<Field>) = this.type.fields.containsAll(interfaceFields)
