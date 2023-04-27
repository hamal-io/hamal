package io.hamal.lib.domain.vo

import io.hamal.lib.domain.vo.base.DomainId
import io.hamal.lib.domain.vo.base.DomainIdSerializer
import io.hamal.lib.util.Snowflake
import kotlinx.serialization.Serializable


@Serializable(with = FlowId.Serializer::class)
class FlowId(override val value: Snowflake.Id) : DomainId() {
    internal object Serializer : DomainIdSerializer<FlowId>(::FlowId)
}