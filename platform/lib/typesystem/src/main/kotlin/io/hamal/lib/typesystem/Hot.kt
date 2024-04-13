package io.hamal.lib.typesystem

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.HotModule
import io.hamal.lib.common.serialization.JsonAdapter
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
    }
}