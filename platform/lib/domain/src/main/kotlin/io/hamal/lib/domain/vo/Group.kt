package io.hamal.lib.domain.vo

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.DomainId
import io.hamal.lib.common.domain.DomainIdSerializer
import io.hamal.lib.common.domain.DomainName
import io.hamal.lib.common.domain.DomainNameSerializer
import kotlinx.serialization.Serializable

@Serializable(with = GroupId.Serializer::class)
class GroupId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))

    internal object Serializer : DomainIdSerializer<GroupId>(::GroupId)
}

@Serializable(with = GroupName.Serializer::class)
class GroupName(override val value: String) : DomainName() {
    internal object Serializer : DomainNameSerializer<GroupName>(::GroupName)
}
