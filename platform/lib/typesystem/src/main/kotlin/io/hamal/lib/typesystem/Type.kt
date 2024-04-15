package io.hamal.lib.typesystem

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.domain.ValueObjectString
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.typesystem.value.ValueList
import io.hamal.lib.typesystem.value.ValueObject

class TypeId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))
}

class TypeIdentifier(override val value: String) : ValueObjectString()

sealed class Type {
    abstract val identifier: String // FIXME TypeIdentifier

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

data object TypeAny : Type() {
    override val identifier = "any"
}

data object TypeDecimal : Type() {
    override val identifier = "decimal"
}


sealed class TypeList : Type() {
    abstract val valueType: Type

    operator fun invoke(vararg any: Any): ValueList {
        return ValueList(any.map { v -> Property.mapTypeToValue(valueType, v) }, valueType)
    }
}

data object TypeListNumber : TypeList() {
    override val identifier: String = "list_number"
    override val valueType: Type = TypeNumber
}

data class TypeListObject(
    override val identifier: String,
    override val valueType: TypeObject
) : TypeList()

data object TypeNil : Type() {
    override val identifier = "nil"
}

data object TypeNumber : Type() {
    override val identifier = "number"
}

// FIXME objects are equal if they have the same fields
data class TypeObject(override val identifier: String, val fields: Set<Field>) : Type() {
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


data object TypeString : Type() {
    override val identifier = "string"
}





