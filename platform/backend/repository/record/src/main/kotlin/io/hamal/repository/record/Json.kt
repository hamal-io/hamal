package io.hamal.repository.record

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.hot.HotJsonModule
import io.hamal.lib.common.serialization.JsonAdapter
import io.hamal.lib.common.serialization.JsonFactoryBuilder
import io.hamal.lib.common.serialization.JsonModule
import io.hamal.lib.common.serialization.ValueObjectStringAdapter
import io.hamal.lib.domain.Json
import io.hamal.lib.domain.vo.ValueObjectJsonModule
import io.hamal.lib.kua.type.KuaJsonModule
import io.hamal.repository.api.DomainJsonModule
import io.hamal.repository.record.account.AccountRecord
import io.hamal.repository.record.blueprint.BlueprintRecord
import io.hamal.repository.record.code.CodeRecord
import io.hamal.repository.record.endpoint.EndpointRecord
import io.hamal.repository.record.exec.ExecRecord
import io.hamal.repository.record.extension.ExtensionRecord
import io.hamal.repository.record.feedback.FeedbackRecord
import io.hamal.repository.record.flow.FlowRecord
import io.hamal.repository.record.func.FuncRecord
import io.hamal.repository.record.group.GroupRecord
import io.hamal.repository.record.hook.HookRecord
import io.hamal.repository.record.topic.TopicRecord
import io.hamal.repository.record.trigger.TriggerRecord
import java.lang.reflect.Type
import kotlin.reflect.KClass

object RecordJsonModule : JsonModule() {
    init {
        this[RecordClass::class] = ValueObjectStringAdapter(::RecordClass)
        this[AccountRecord::class] = AccountRecord.Adapter
        this[BlueprintRecord::class] = BlueprintRecord.Adapter
        this[CodeRecord::class] = CodeRecord.Adapter
        this[EndpointRecord::class] = EndpointRecord.Adapter
        this[ExecRecord::class] = ExecRecord.Adapter
        this[ExtensionRecord::class] = ExtensionRecord.Adapter
        this[FeedbackRecord::class] = FeedbackRecord.Adapter
        this[FlowRecord::class] = FlowRecord.Adapter
        this[FuncRecord::class] = FuncRecord.Adapter
        this[GroupRecord::class] = GroupRecord.Adapter
        this[HookRecord::class] = HookRecord.Adapter
        this[TopicRecord::class] = TopicRecord.Adapter
        this[TriggerRecord::class] = TriggerRecord.Adapter
    }
}

val json = Json(
    JsonFactoryBuilder()
        .register(DomainJsonModule)
        .register(HotJsonModule)
        .register(KuaJsonModule)
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