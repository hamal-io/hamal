package io.hamal.lib.typesystem

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.domain.ValueObjectString
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.typesystem.Field.Kind.Object
import io.hamal.lib.typesystem.value.ValueList
import io.hamal.lib.typesystem.value.ValueObject

class TypeId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))
}

class TypeIdentifier(override val value: String) : ValueObjectString()

interface TypeNew {
    val identifier: String // FIXME TypeIdentifier
}

data class Type(
    val identifier: String,
    val fields: Set<Field>,
) {
    constructor(identifier: String, vararg fields: Field) : this(identifier, fields.toSet())

    init {
        // prevent multiple definitions of fields
        val repeatedFields = fields.groupBy { it.identifier }.mapValues { it.value.size }.filterValues { it > 1 }
        if (repeatedFields.any()) {
            throw IllegalArgumentException("Duplicate field definitions detected: [${repeatedFields.keys.joinToString(",")}]")
        }
    }

    operator fun invoke(vararg args: Any, kwargs: Map<String, Any> = emptyMap()) = ValueObject(this, args.zip(fields).map { (value, field) ->
        Property.of(field, value)
    } + kwargs.mapNotNull { (k, v) ->
        fieldsByIdentifier[k]?.let { Property.of(it, v) }
    }
    )

    val fieldsByIdentifier by lazy {
        fields.associateBy(Field::identifier)
    }
}

object TypeNumber : TypeNew {
    override val identifier = "number"
}


data class TypeList(
    override val identifier: String,
    val field: Field
) : TypeNew {
    constructor(identifier: String, kind: Field.Kind, valueType: Type? = null) : this(identifier, Field(kind, "value", valueType))
    constructor(identifier: String, valueType: Type) : this(identifier, Field(Object, "value", valueType))

    operator fun invoke(vararg any: Any): ValueList {
        return ValueList(
            any.map { v ->
                Property.mapTypeToValue(field.valueType!!, v)
            }, field.valueType!!
        )
    }
}


infix fun Type.extends(superclass: Type): Type {
    val fieldClashes = fieldsByIdentifier.keys.intersect(superclass.fieldsByIdentifier.keys)

    if (fieldClashes.any()) {
        throw IllegalArgumentException(fieldClashes.joinToString(System.lineSeparator()) {
            "Field $it defined in type `$identifier` also defined in superclass `${superclass.identifier}`"
        })
    }

    return Type(identifier, superclass.fields + fields)
}

infix fun Type.extends(superclasses: Iterable<Type>) =
    (listOf(this) + superclasses).reduceRight { acc, that -> that extends acc }

