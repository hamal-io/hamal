package io.hamal.backend.instance.config

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.base.DomainId
import org.springframework.core.convert.converter.Converter
import kotlin.reflect.KClass


object LimitConverter : Converter<String, Limit> {
    override fun convert(source: String) = Limit(source.toInt())
}

object CorrelationIdConverter : Converter<String, CorrelationId> {
    override fun convert(source: String) = CorrelationId(source)
}

object EventIdConverter : DomainIdConverter<EventId>(EventId::class, ::EventId)
object ExecIdConverter : DomainIdConverter<ExecId>(ExecId::class, ::ExecId)
object FuncIdConverter : DomainIdConverter<FuncId>(FuncId::class, ::FuncId)
object ReqIdConverter : DomainIdConverter<ReqId>(ReqId::class, ::ReqId)
object TopicIdConverter : DomainIdConverter<TopicId>(TopicId::class, ::TopicId)

sealed class DomainIdConverter<ID : DomainId>(
    val clazz: KClass<ID>,
    val ctor: (SnowflakeId) -> ID,
) : Converter<String, ID> {
    override fun convert(source: String): ID {
        return ctor(SnowflakeId(source.toLong()))
    }
}
