package io.hamal.backend.instance.config.converter

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.base.DomainId
import org.springframework.core.convert.converter.Converter
import kotlin.reflect.KClass

class TopicIdConverter : DomainIdConverter<TopicId>(TopicId::class, ::TopicId)

sealed class DomainIdConverter<ID : DomainId>(
    val clazz: KClass<ID>,
    val ctor: (SnowflakeId) -> ID,
) : Converter<String, ID> {
    override fun convert(source: String): ID? {
        return ctor(SnowflakeId(source.toLong()))
    }
}
