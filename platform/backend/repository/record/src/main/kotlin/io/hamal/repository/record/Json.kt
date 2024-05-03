package io.hamal.repository.record

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.AdapterJson
import io.hamal.lib.common.serialization.Serde
import io.hamal.lib.common.serialization.SerdeModuleJson
import io.hamal.lib.common.value.serde.SerdeModuleJsonValue
import io.hamal.lib.common.value.serde.ValueJsonAdapters
import io.hamal.lib.domain.vo.SerdeModuleJsonValueVariable
import io.hamal.repository.api.SerdeModuleJsonDomain
import io.hamal.repository.record.account.AccountRecord
import io.hamal.repository.record.code.CodeRecord
import io.hamal.repository.record.exec.ExecRecord
import io.hamal.repository.record.extension.ExtensionRecord
import io.hamal.repository.record.feedback.FeedbackRecord
import io.hamal.repository.record.func.FuncRecord
import io.hamal.repository.record.namespace.NamespaceRecord
import io.hamal.repository.record.namespace_tree.NamespaceTreeRecord
import io.hamal.repository.record.recipe.RecipeRecord
import io.hamal.repository.record.topic.TopicRecord
import io.hamal.repository.record.trigger.TriggerRecord
import io.hamal.repository.record.workspace.WorkspaceRecord
import java.lang.reflect.Type
import kotlin.reflect.KClass

object SerdeModuleJsonRecord : SerdeModuleJson() {
    init {
        this[RecordClass::class] = ValueJsonAdapters.StringVariable(::RecordClass)
        this[RecordedAt::class] = ValueJsonAdapters.InstantVariable(::RecordedAt)
        this[AccountRecord::class] = AccountRecord.Adapter
        this[RecipeRecord::class] = RecipeRecord.Adapter
        this[CodeRecord::class] = CodeRecord.Adapter
        this[ExecRecord::class] = ExecRecord.Adapter
        this[ExtensionRecord::class] = ExtensionRecord.Adapter
        this[FeedbackRecord::class] = FeedbackRecord.Adapter
        this[NamespaceRecord::class] = NamespaceRecord.Adapter
        this[NamespaceTreeRecord::class] = NamespaceTreeRecord.Adapter
        this[FuncRecord::class] = FuncRecord.Adapter
        this[WorkspaceRecord::class] = WorkspaceRecord.Adapter
        this[TopicRecord::class] = TopicRecord.Adapter
        this[TriggerRecord::class] = TriggerRecord.Adapter
    }
}

val serde = Serde.json()
    .register(SerdeModuleJsonDomain)
    .register(SerdeModuleJsonRecord)
    .register(SerdeModuleJsonValue)
    .register(SerdeModuleJsonValueVariable)

abstract class RecordAdapter<BASE_TYPE : Record<*>>(
    recordClasses: List<KClass<out BASE_TYPE>>
) : AdapterJson<BASE_TYPE> {
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