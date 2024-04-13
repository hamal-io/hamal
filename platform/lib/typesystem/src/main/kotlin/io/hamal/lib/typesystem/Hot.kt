package io.hamal.lib.typesystem

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.HotModule
import io.hamal.lib.common.serialization.JsonAdapter
import io.hamal.lib.typesystem.value.ValueString
import java.lang.reflect.Type

object TypesystemHotModule : HotModule() {
    init {
        this[TypeNew::class] = object : JsonAdapter<TypeNew> {
            override fun serialize(src: TypeNew?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
                return JsonPrimitive("TypeString")
            }

            override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): TypeNew {
                return TypeString
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