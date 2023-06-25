package io.hamal.backend.instance.config

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.vo.EventId
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.Limit
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.base.DomainId
import org.springframework.core.convert.converter.Converter
import kotlin.reflect.KClass


object LimitConverter : Converter<String, Limit> {
    override fun convert(source: String) = Limit(source.toInt())
}

object EventIdConverter : DomainIdConverter<EventId>(EventId::class, ::EventId)
object ExecIdConverter : DomainIdConverter<ExecId>(ExecId::class, ::ExecId)
object TopicIdConverter : DomainIdConverter<TopicId>(TopicId::class, ::TopicId)

sealed class DomainIdConverter<ID : DomainId>(
    val clazz: KClass<ID>,
    val ctor: (SnowflakeId) -> ID,
) : Converter<String, ID> {
    override fun convert(source: String): ID? {
        return ctor(SnowflakeId(source.toLong()))
    }
}
