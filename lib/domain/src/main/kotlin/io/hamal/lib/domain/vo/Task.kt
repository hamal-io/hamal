package io.hamal.lib.domain.vo

import io.hamal.lib.domain.util.SnowflakeId
import io.hamal.lib.domain.vo.base.DomainId
import io.hamal.lib.domain.vo.base.DomainIdSerializer
import kotlinx.serialization.Serializable

@Serializable(with = TaskId.Serializer::class)
class TaskId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))

    internal object Serializer : DomainIdSerializer<TaskId>(::TaskId)
}
