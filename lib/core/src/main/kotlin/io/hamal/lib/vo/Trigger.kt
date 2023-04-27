package io.hamal.lib.vo

import io.hamal.lib.util.Snowflake
import io.hamal.lib.vo.base.DomainId
import io.hamal.lib.vo.base.DomainIdSerializer
import kotlinx.serialization.Serializable


@Serializable(with = TriggerId.Serializer::class)
class TriggerId(override val value: Snowflake.Id) : DomainId() {
    internal object Serializer : DomainIdSerializer<TriggerId>(::TriggerId)
}
