package io.hamal.core.component

import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.snowflake.SnowflakeId
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

object CorrelationIdConverter : Converter<String, CorrelationId> {
    override fun convert(source: String) = CorrelationId(source)
}

object TopicNameConverter : Converter<String, TopicName> {
    override fun convert(source: String) = TopicName(source)
}

object AccountIdConverter : DomainIdConverter<AccountId>(AccountId::class, ::AccountId)
object CodeIdConverter : DomainIdConverter<CodeId>(CodeId::class, ::CodeId)
object ExecIdConverter : DomainIdConverter<ExecId>(ExecId::class, ::ExecId)
object ExecLogIdConverter : DomainIdConverter<ExecLogId>(ExecLogId::class, ::ExecLogId)
object FuncIdConverter : DomainIdConverter<FuncId>(FuncId::class, ::FuncId)
object GroupIdConverter : DomainIdConverter<GroupId>(GroupId::class, ::GroupId)
object FlowIdConverter : DomainIdConverter<FlowId>(FlowId::class, ::FlowId)
object ReqIdConverter : DomainIdConverter<RequestId>(RequestId::class, ::RequestId)
object TopicIdConverter : DomainIdConverter<TopicId>(TopicId::class, ::TopicId)
object TopicEntryIdConverter : DomainIdConverter<TopicEntryId>(TopicEntryId::class, ::TopicEntryId)
object TriggerIdConverter : DomainIdConverter<TriggerId>(TriggerId::class, ::TriggerId)

sealed class DomainIdConverter<ID : ValueObjectId>(
    val clazz: KClass<ID>,
    val ctor: (SnowflakeId) -> ID,
) : Converter<String, ID> {
    override fun convert(source: String): ID {
        return ctor(SnowflakeId(source))
    }
}
