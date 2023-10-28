package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.DomainName
import io.hamal.lib.common.domain.DomainNameSerializer
import io.hamal.lib.common.snowflake.SnowflakeId
import kotlinx.serialization.Serializable

@Serializable(with = ExtensionId.Serializer::class)
class ExtensionId(override val value: SnowflakeId) : SerializableDomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))

    internal object Serializer : DomainIdSerializer<ExtensionId>(::ExtensionId)
}


@Serializable(with = ExtensionName.Serializer::class)
class ExtensionName(override val value: String) : DomainName() {
    internal object Serializer : DomainNameSerializer<ExtensionName>(::ExtensionName)
}


