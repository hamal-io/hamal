package io.hamal.lib.typesystem.value

import io.hamal.lib.typesystem.Field
import io.hamal.lib.typesystem.Kind
import io.hamal.lib.typesystem.Property
import io.hamal.lib.typesystem.Type

data class ObjectValue(
    val type: Type,
    val properties: List<Property>,
) : Value {
    override val kind get() = Kind.Object

    operator fun <T : Value> get(identifier: String) = valuesByIdentifier[identifier] as T

    override fun toString() = "${type.name}(${properties.joinToString(", ") { it.toString() }})"

    private val valuesByIdentifier = properties.associateBy { it.identifier }.mapValues { it.value.value }
}

fun <T : Value> ObjectValue.forType(type: Type, block: () -> T) =
    if (!this.implements(type.fields)) throw NotImplementedError()
    else block()

infix fun ObjectValue.implements(interfaceFields: Set<Field>) = this.type.fields.containsAll(interfaceFields)
