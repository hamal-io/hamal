package io.hamal.lib.typesystem

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.domain.ValueObjectString
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.typesystem.value.ObjectValue
import io.hamal.lib.typesystem.value.ValueString
import io.hamal.lib.typesystem.value.implements
import java.time.LocalDate
import java.util.*

class TypeId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))
}

class TypeName(override val value: String) : ValueObjectString()

data class Type(
    val identifier: String,
    val fields: Set<Field>,
    val name: String = identifier.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
) {
    constructor(
        identifier: String,
        vararg fields: Field,
        name: String = identifier.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    ) :
            this(identifier, fields.toSet(), name)

    init {
        // prevent multiple definitions of fields
        val repeatedFields = fields.groupBy { it.identifier }.mapValues { it.value.size }.filterValues { it > 1 }
        if (repeatedFields.any()) {
            throw IllegalArgumentException("Duplicate field definitions detected: [${repeatedFields.keys.joinToString(",")}]")
        }
    }

    operator fun invoke(vararg args: Any, kwargs: Map<String, Any> = emptyMap()) = ObjectValue(
        this,
        args.zip(fields).map { (value, field) ->
            Property(field, value)
        } + kwargs.mapNotNull { (k, v) ->
            fieldsByIdentifier[k]?.let {
                Property(it, v)
            }

        }
    )

    val fieldsByIdentifier by lazy {
        fields.associateBy(Field::identifier)
    }
}


infix fun Type.extends(superclass: Type): Type {

    val fieldClashes = fieldsByIdentifier.keys.intersect(superclass.fieldsByIdentifier.keys)
    if (fieldClashes.any()) {
        throw IllegalArgumentException(fieldClashes.joinToString(System.lineSeparator()) {
            "Field $it defined in type `$name` also defined in superclass `${superclass.name}`"
        })
    }

    return Type(
        identifier,
        superclass.fields + fields,
        name
    )
}

infix fun Type.extends(superclasses: Iterable<Type>) =
    (listOf(this) + superclasses).reduceRight { acc, that -> that extends acc }

