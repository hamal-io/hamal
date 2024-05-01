package io.hamal.lib.common.value

import io.hamal.lib.common.value.Property.Companion.Property


// FIXME objects are equal if they have the same fields
data class TypeObject(override val identifier: TypeIdentifier, val fields: Set<Field>) : Type() {
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
        } + kwargs.mapNotNull { (k, v) -> fieldsByIdentifier[FieldIdentifier(k)]?.let { Property(it, v) } })

    val fieldsByIdentifier by lazy {
        fields.associateBy(Field::identifier)
    }
}

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


data class TypeListObject(
    override val identifier: TypeIdentifier,
    override val valueType: TypeObject
) : TypeList() {
    constructor(identifier: String, valueType: TypeObject) : this(TypeIdentifier(identifier), valueType)
}


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
