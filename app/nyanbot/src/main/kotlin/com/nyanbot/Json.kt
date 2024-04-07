package com.nyanbot

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.nyanbot.repository.impl.account.AccountRecord
import com.nyanbot.repository.record.Record
import com.nyanbot.repository.record.RecordClass
import io.hamal.lib.common.hot.HotObjectModule
import io.hamal.lib.common.serialization.HotModule
import io.hamal.lib.common.serialization.JsonAdapter
import io.hamal.lib.common.serialization.JsonFactoryBuilder
import io.hamal.lib.common.serialization.ValueObjectStringAdapter
import io.hamal.lib.domain.Json
import io.hamal.lib.domain.vo.ValueObjectJsonModule
import io.hamal.lib.sdk.api.ApiJsonModule
import java.lang.reflect.Type
import kotlin.reflect.KClass

object RecordJsonModule : HotModule() {
    init {
        this[RecordClass::class] = ValueObjectStringAdapter(::RecordClass)
        this[AccountRecord::class] = AccountRecord.Adapter
    }
}

val json = Json(
    JsonFactoryBuilder()
        .register(ApiJsonModule)

        .register(HotObjectModule)
        .register(RecordJsonModule)
        .register(ValueObjectJsonModule)
)

abstract class RecordAdapter<BASE_TYPE : Record<*>>(
    recordClasses: List<KClass<out BASE_TYPE>>
) : JsonAdapter<BASE_TYPE> {
    override fun serialize(src: BASE_TYPE, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return context.serialize(src)
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): BASE_TYPE {
        val recordClass = json.asJsonObject.get("recordClass").asString
        return context.deserialize(
            json, (classMapping[recordClass]
                ?: throw NotImplementedError("$recordClass not supported")).java
        )
    }

    private val classMapping = recordClasses.associateBy { it.simpleName }
}