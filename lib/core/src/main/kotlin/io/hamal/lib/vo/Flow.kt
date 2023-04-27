package io.hamal.lib.vo

import io.hamal.lib.util.Snowflake
import io.hamal.lib.vo.base.DomainId
import io.hamal.lib.vo.base.DomainIdSerializer
import kotlinx.serialization.Serializable


@Serializable(with = FlowId.Serializer::class)
class FlowId(override val value: Snowflake.Id) : DomainId() {
    internal object Serializer : DomainIdSerializer<FlowId>(::FlowId)
}