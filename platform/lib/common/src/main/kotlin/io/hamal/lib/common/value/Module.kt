package io.hamal.lib.common.value

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.JsonAdapter
import io.hamal.lib.common.serialization.SerializationModule
import io.hamal.lib.common.value.TypeIdentifier.Companion.TypeIdentifier
import java.lang.reflect.Type

object ValueJsonModule : SerializationModule() {

    init {
        this[FieldIdentifier::class] = ValueJsonAdapters.StringVariable(::FieldIdentifier)

        this[io.hamal.lib.common.value.Type::class] = object : JsonAdapter<io.hamal.lib.common.value.Type> {
            override fun serialize(
                src: io.hamal.lib.common.value.Type,
                typeOfSrc: Type,
                context: JsonSerializationContext
            ): JsonElement {
                return JsonPrimitive(src.identifier.stringValue)
            }

            override fun deserialize(
                json: JsonElement,
                typeOfT: Type,
                context: JsonDeserializationContext
            ): io.hamal.lib.common.value.Type {
                return when (TypeIdentifier(json.asString)) {
                    TypeBoolean.identifier -> TypeBoolean
                    TypeNumber.identifier -> TypeNumber
                    TypeString.identifier -> TypeString
                    else -> TODO("Not implemented ${json.asString}")
                }
            }
        }

        this[ValueBoolean::class] = ValueJsonAdapters.Boolean
        this[ValueInstant::class] = ValueJsonAdapters.Instant
        this[ValueNumber::class] = ValueJsonAdapters.Number
        this[ValueObject::class] = ValueJsonAdapters.Object
        this[ValueSnowflakeId::class] = ValueJsonAdapters.SnowflakeId
        this[ValueString::class] = ValueJsonAdapters.String

    }
}