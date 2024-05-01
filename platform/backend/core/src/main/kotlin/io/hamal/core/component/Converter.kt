package io.hamal.core.component

import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.ValueSnowflakeId
import io.hamal.lib.common.value.ValueVariableSnowflakeId
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.CorrelationId.Companion.CorrelationId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.domain.vo.TopicName.Companion.TopicName
import org.springframework.core.convert.converter.Converter
import kotlin.reflect.KClass


object LimitConverter : Converter<String, Limit> {
    override fun convert(source: String) = Limit(source.toInt())
}

object CodeVersionConverter : Converter<String, CodeVersion> {
    override fun convert(source: String) = CodeVersion(source.toInt())
}

object CorrelationIdConverter : Converter<String, CorrelationId> {
    override fun convert(source: String) = CorrelationId(source)
}

object TopicNameConverter : Converter<String, TopicName> {
    override fun convert(source: String) = TopicName(source)
}

data object AccountIdConverter : ValueVariableSnowflakeIdConverter<AccountId>(AccountId::class, ::AccountId)
data object CodeIdConverter : ValueVariableSnowflakeIdConverter<CodeId>(CodeId::class, ::CodeId)
data object ExecIdConverter : ValueVariableSnowflakeIdConverter<ExecId>(ExecId::class, ::ExecId)
data object ExtensionIdConverter : ValueVariableSnowflakeIdConverter<ExtensionId>(ExtensionId::class, ::ExtensionId)
data object ExecLogIdConverter : ValueVariableSnowflakeIdConverter<ExecLogId>(ExecLogId::class, ::ExecLogId)
data object FeedbackIdConverter : ValueVariableSnowflakeIdConverter<FeedbackId>(FeedbackId::class, ::FeedbackId)
data object FuncIdConverter : ValueVariableSnowflakeIdConverter<FuncId>(FuncId::class, ::FuncId)
data object NamespaceIdConverter : ValueVariableSnowflakeIdConverter<NamespaceId>(NamespaceId::class, ::NamespaceId)
data object RequestIdConverter : ValueVariableSnowflakeIdConverter<RequestId>(RequestId::class, ::RequestId)
data object RecipeIdConverter : ValueVariableSnowflakeIdConverter<RecipeId>(RecipeId::class, ::RecipeId)
data object TopicIdConverter : ValueVariableSnowflakeIdConverter<TopicId>(TopicId::class, ::TopicId)
data object TopicEntryIdConverter : ValueVariableSnowflakeIdConverter<TopicEventId>(TopicEventId::class, ::TopicEventId)
data object TriggerIdConverter : ValueVariableSnowflakeIdConverter<TriggerId>(TriggerId::class, ::TriggerId)
data object WorkspaceIdConverter : ValueVariableSnowflakeIdConverter<WorkspaceId>(WorkspaceId::class, ::WorkspaceId)

sealed class ValueVariableSnowflakeIdConverter<ID : ValueVariableSnowflakeId>(
    val clazz: KClass<ID>,
    val ctor: (ValueSnowflakeId) -> ID,
) : Converter<String, ID> {
    override fun convert(src: String): ID {
        return ctor(ValueSnowflakeId(SnowflakeId(src)))
    }
}
