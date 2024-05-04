package io.hamal.repository.record

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.AdapterGeneric
import io.hamal.lib.common.serialization.Serde
import io.hamal.lib.common.serialization.SerdeModuleGeneric
import io.hamal.lib.common.value.serde.SerdeModuleValueHon
import io.hamal.lib.common.value.serde.ValueVariableAdapters
import io.hamal.lib.domain.vo.SerdeModuleValueVariable
import io.hamal.repository.api.SerdeModuleDomain
import io.hamal.repository.record.RecordClass.Companion.RecordClass
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

private object SerdeModuleRecord : SerdeModuleGeneric() {
    init {
        this[RecordClass::class] = ValueVariableAdapters.String(::RecordClass)
        this[RecordedAt::class] = ValueVariableAdapters.Instant(::RecordedAt)
        this[AccountRecord::class] = AccountRecord.Adapter
        this[CodeRecord::class] = CodeRecord.Adapter
        this[ExecRecord::class] = ExecRecord.Adapter
        this[ExtensionRecord::class] = ExtensionRecord.Adapter
        this[FeedbackRecord::class] = FeedbackRecord.Adapter
        this[NamespaceRecord::class] = NamespaceRecord.Adapter
        this[NamespaceTreeRecord::class] = NamespaceTreeRecord.Adapter
        this[FuncRecord::class] = FuncRecord.Adapter
        this[RecipeRecord::class] = RecipeRecord.Adapter
        this[WorkspaceRecord::class] = WorkspaceRecord.Adapter
        this[TopicRecord::class] = TopicRecord.Adapter
        this[TriggerRecord::class] = TriggerRecord.Adapter
    }
}

val serde = Serde.hon()
    .register(SerdeModuleDomain)
    .register(SerdeModuleRecord)
    .register(SerdeModuleValueHon)
    .register(SerdeModuleValueVariable)

abstract class RecordAdapter<BASE_TYPE : Record<*>>(
    recordClasses: List<KClass<out BASE_TYPE>>
) : AdapterGeneric<BASE_TYPE> {
    override fun serialize(src: BASE_TYPE, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return context.serialize(src)
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): BASE_TYPE {
        val recordClass = context.deserialize<RecordClass>(
            json.asJsonObject.get("recordClass"), RecordClass::class.java
        )

        return context.deserialize(json, (classMapping[recordClass] ?: throw NotImplementedError("$recordClass not supported")).java)
    }

    private val classMapping = recordClasses.associateBy { RecordClass(it.simpleName!!) }
}