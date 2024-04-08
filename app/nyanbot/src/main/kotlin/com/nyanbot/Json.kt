package com.nyanbot

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.nyanbot.repository.FlowId
import com.nyanbot.repository.FlowName
import com.nyanbot.repository.FlowTriggerId
import com.nyanbot.repository.FlowTriggerType
import com.nyanbot.repository.impl.account.AccountRecord
import com.nyanbot.repository.impl.flow.FlowRecord
import com.nyanbot.repository.record.Record
import com.nyanbot.repository.record.RecordClass
import io.hamal.lib.common.hot.HotObjectModule
import io.hamal.lib.common.serialization.*
import io.hamal.lib.domain.Json
import io.hamal.lib.domain.vo.ValueObjectJsonModule
import io.hamal.lib.sdk.api.ApiJsonModule
import java.lang.reflect.Type
import kotlin.reflect.KClass

object RecordJsonModule : HotModule() {
    init {
        this[RecordClass::class] = ValueObjectStringAdapter(::RecordClass)
        this[AccountRecord::class] = AccountRecord.Adapter
        this[FlowRecord::class] = FlowRecord.Adapter
    }
}

object DomainModule : HotModule() {
    init {
        this[FlowId::class] = ValueObjectIdAdapter(::FlowId)
        this[FlowName::class] = ValueObjectStringAdapter(::FlowName)
        this[FlowTriggerId::class] = ValueObjectIdAdapter(::FlowTriggerId)
        this[FlowTriggerType::class] = ValueObjectStringAdapter(::FlowTriggerType)
    }
}

val json = Json(
    JsonFactoryBuilder()
        .register(ApiJsonModule)
        .register(HotObjectModule)
        .register(RecordJsonModule)
        .register(DomainModule)
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