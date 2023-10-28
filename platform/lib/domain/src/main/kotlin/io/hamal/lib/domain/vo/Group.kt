package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.DomainName
import io.hamal.lib.common.domain.DomainNameSerializer
import io.hamal.lib.common.snowflake.SnowflakeId
import kotlinx.serialization.Serializable

@Serializable(with = GroupId.Serializer::class)
class GroupId(override val value: SnowflakeId) : SerializableDomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))

    internal object Serializer : SerializableDomainIdSerializer<GroupId>(::GroupId)

    companion object {
        val root = GroupId(1)
    }
}

@Serializable(with = GroupName.Serializer::class)
class GroupName(override val value: String) : DomainName() {
    internal object Serializer : DomainNameSerializer<GroupName>(::GroupName)
}
