package io.hamal.lib.common.value

import io.hamal.lib.common.value.FieldIdentifier.Companion.FieldIdentifier
import io.hamal.lib.common.value.Property.Companion.Property
import io.hamal.lib.common.value.TypeIdentifier.Companion.TypeIdentifier
import java.math.BigDecimal
import java.math.BigInteger


// FIXME objects are equal if they have the same fields


data class TypeObject(
    override val identifier: TypeIdentifier,
    val fields: Set<Field>
) : Type() {
    constructor(identifier: TypeIdentifier, vararg fields: Field) : this(identifier, fields.toSet())
    constructor(identifier: String, vararg fields: Field) : this(TypeIdentifier(identifier), fields.toSet())

    init {
        // prevent multiple definitions of fields
        val repeatedFields = fields.groupBy { it.identifier }.mapValues { it.value.size }.filterValues { it > 1 }
        if (repeatedFields.any()) {
            throw IllegalArgumentException("Duplicate field definitions detected: [${repeatedFields.keys.joinToString(",")}]")
        }
    }

    operator fun invoke(vararg args: Any, kwargs: Map<String, Any> = emptyMap()) =
        ValueObject(this, args.zip(fields).map { (value, field) ->
            Property(field, value)
        } + kwargs.mapNotNull { (k, v) -> fieldsByIdentifier[FieldIdentifier(ValueString(k))]?.let { Property(it, v) } })

    val fieldsByIdentifier by lazy {
        fields.associateBy(Field::identifier)
    }
}

val TypeObjectUnknown = TypeObject("Unknown")

infix fun TypeObject.extends(superclass: TypeObject): TypeObject {
    val fieldClashes = fieldsByIdentifier.keys.intersect(superclass.fieldsByIdentifier.keys)

    if (fieldClashes.any()) {
        throw IllegalArgumentException(fieldClashes.joinToString(System.lineSeparator()) {
            "Field $it defined in type `$identifier` also defined in superclass `${superclass.identifier}`"
        })
    }

    return TypeObject(identifier, superclass.fields + fields)
}

infix fun TypeObject.extends(superclasses: Iterable<TypeObject>) =
    (listOf(this) + superclasses).reduceRight { acc, that -> that extends acc }

data class ValueObject(
    override val type: TypeObject,
    val properties: List<Property>,
) : Value {

    fun stringValue(key: String): String = (get(key) as ValueString).stringValue

    operator fun get(identifier: FieldIdentifier): Value = valuesByIdentifier[identifier] ?: ValueNil

    operator fun get(identifier: String): Value = get(FieldIdentifier(ValueString(identifier)))

    override fun toString() = "${type.identifier}(${properties.joinToString(", ") { it.toString() }})"

    private val valuesByIdentifier = properties.associateBy { it.identifier }.mapValues { it.value.value }

    companion object {
        val empty = ValueObject(TypeObjectUnknown, emptyList())
        fun builder(): ValueObjectBuilder = ValueObjectBuilder()
    }
}

abstract class ValueVariableObject : ValueVariable.BaseImpl<ValueObject>()

fun <T : Value> ValueObject.forType(type: TypeObject, block: () -> T) =
    if (!this.implements(type.fields)) throw NotImplementedError()
    else block()

infix fun ValueObject.implements(interfaceFields: Set<Field>) = type is TypeObject && this.type.fields.containsAll(interfaceFields)

class ValueObjectBuilder {

    operator fun set(key: String, value: Enum<*>): ValueObjectBuilder {
        values[key] = ValueString(value.name)
        return this
    }

    operator fun set(key: String, value: Value): ValueObjectBuilder {
        values[key] = value
        return this
    }

    operator fun set(key: String, value: ValueVariable<*>): ValueObjectBuilder {
        values[key] = value.value
        return this
    }

    operator fun set(key: String, value: String): ValueObjectBuilder {
        values[key] = ValueString(value)
        return this
    }

    operator fun set(key: String, value: Byte): ValueObjectBuilder {
        values[key] = ValueNumber(value)
        return this
    }

    operator fun set(key: String, value: Short): ValueObjectBuilder {
        values[key] = ValueNumber(value)
        return this
    }

    operator fun set(key: String, value: BigInteger): ValueObjectBuilder {
        values[key] = ValueNumber(value)
        return this
    }

    operator fun set(key: String, value: BigDecimal): ValueObjectBuilder {
        values[key] = ValueNumber(value)
        return this
    }

    operator fun set(key: String, value: Int): ValueObjectBuilder {
        values[key] = ValueNumber(value)
        return this
    }

    operator fun set(key: String, value: Float): ValueObjectBuilder {
        values[key] = ValueNumber(value)
        return this
    }

    operator fun set(key: String, value: Double): ValueObjectBuilder {
        values[key] = ValueNumber(value)
        return this
    }

    operator fun set(key: String, value: Long): ValueObjectBuilder {
        values[key] = ValueNumber(value)
        return this
    }

    operator fun set(key: String, value: Boolean): ValueObjectBuilder {
        values[key] = ValueBoolean(value)
        return this
    }

    fun setNil(key: String): ValueObjectBuilder {
        values[key] = ValueNil
        return this
    }

    fun build(type: TypeObject = TypeObjectUnknown): ValueObject {
        // FIXME if type != Unknown validate fields
        val properties = values.map { (key, value) -> Property(FieldIdentifier(key), value) }
        return ValueObject(type, properties)
    }

    private val values = mutableMapOf<String, Value>()
}