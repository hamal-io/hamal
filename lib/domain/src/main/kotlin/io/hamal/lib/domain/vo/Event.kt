package io.hamal.lib.domain.vo

import io.hamal.lib.domain.vo.base.DomainName
import io.hamal.lib.domain.vo.base.DomainNameSerializer
import kotlinx.serialization.Serializable


@Serializable
data class TopicId(val value: Int) // FIXME must become VO

@Serializable(with = TopicName.Serializer::class)
class TopicName(override val value: String) : DomainName() {
    internal object Serializer : DomainNameSerializer<TopicName>(::TopicName)
}
