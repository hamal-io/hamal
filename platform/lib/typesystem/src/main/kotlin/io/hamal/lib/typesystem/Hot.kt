package io.hamal.lib.typesystem

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.HotModule
import io.hamal.lib.common.serialization.JsonAdapter
import io.hamal.lib.typesystem.type.TypeBoolean
import io.hamal.lib.typesystem.type.TypeIdentifier
import io.hamal.lib.typesystem.type.TypeNumber
import io.hamal.lib.typesystem.type.TypeString
import io.hamal.lib.typesystem.value.ValueBoolean
import io.hamal.lib.typesystem.value.ValueFalse
import io.hamal.lib.typesystem.value.ValueString
import io.hamal.lib.typesystem.value.ValueTrue
import java.lang.reflect.Type

object TypesystemHotModule : HotModule() {
    init {

        this[io.hamal.lib.typesystem.type.Type::class] = object : JsonAdapter<io.hamal.lib.typesystem.type.Type> {
            override fun serialize(src: io.hamal.lib.typesystem.type.Type, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
                return JsonPrimitive(src.identifier.value)
            }

            override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): io.hamal.lib.typesystem.type.Type {
                return when (TypeIdentifier(json.asString)) {
                    TypeBoolean.identifier -> TypeBoolean
                    TypeNumber.identifier -> TypeNumber
                    TypeString.identifier -> TypeString
                    else -> TODO()
                }
            }
        }
        this[ValueBoolean::class] = object : JsonAdapter<ValueBoolean> {
            override fun serialize(p0: ValueBoolean, p1: Type, p2: JsonSerializationContext): JsonElement {
                return JsonPrimitive(p0.booleanValue)
            }

            override fun deserialize(p0: JsonElement, p1: Type, p2: JsonDeserializationContext): ValueBoolean {
                return if (p0.asBoolean) ValueTrue else ValueFalse
            }

        }
        this[ValueString::class] = object : JsonAdapter<ValueString> {
            override fun serialize(p0: ValueString, p1: Type?, p2: JsonSerializationContext?): JsonElement {
                return JsonPrimitive(p0.stringValue)
            }

            override fun deserialize(p0: JsonElement, p1: Type, p2: JsonDeserializationContext): ValueString {
                return ValueString(p0.asString)
            }
        }
    }
}