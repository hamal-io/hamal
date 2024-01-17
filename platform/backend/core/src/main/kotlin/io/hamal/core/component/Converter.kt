package io.hamal.core.component

import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain._enum.FeedbackMood
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.RequestId
import org.springframework.core.convert.converter.Converter
import kotlin.reflect.KClass


object LimitConverter : Converter<String, Limit> {
    override fun convert(source: String) = Limit(source.toInt())
}

object CodeVersionConverter : Converter<String, CodeVersion> {
    override fun convert(source: String) = CodeVersion(source.toInt())
}

object FeedbackMoodConverter : Converter<String, FeedbackMood> {
    override fun convert(source: String): FeedbackMood {
        return FeedbackMood.of(source.toInt())
    }
}

object CorrelationIdConverter : Converter<String, CorrelationId> {
    override fun convert(source: String) = CorrelationId(source)
}

object TopicNameConverter : Converter<String, TopicName> {
    override fun convert(source: String) = TopicName(source)
}

object AccountIdConverter : ValueObjectIdConverter<AccountId>(AccountId::class, ::AccountId)
object CodeIdConverter : ValueObjectIdConverter<CodeId>(CodeId::class, ::CodeId)
object ExecIdConverter : ValueObjectIdConverter<ExecId>(ExecId::class, ::ExecId)
object ExecLogIdConverter : ValueObjectIdConverter<ExecLogId>(ExecLogId::class, ::ExecLogId)
object FuncIdConverter : ValueObjectIdConverter<FuncId>(FuncId::class, ::FuncId)
object GroupIdConverter : ValueObjectIdConverter<GroupId>(GroupId::class, ::GroupId)
object FlowIdConverter : ValueObjectIdConverter<FlowId>(FlowId::class, ::FlowId)
object ReqIdConverter : ValueObjectIdConverter<RequestId>(RequestId::class, ::RequestId)
object TopicIdConverter : ValueObjectIdConverter<TopicId>(TopicId::class, ::TopicId)
object TopicEntryIdConverter : ValueObjectIdConverter<TopicEntryId>(TopicEntryId::class, ::TopicEntryId)
object TriggerIdConverter : ValueObjectIdConverter<TriggerId>(TriggerId::class, ::TriggerId)

sealed class ValueObjectIdConverter<ID : ValueObjectId>(
    val clazz: KClass<ID>,
    val ctor: (SnowflakeId) -> ID,
) : Converter<String, ID> {
    override fun convert(source: String): ID {
        return ctor(SnowflakeId(source))
    }
}
