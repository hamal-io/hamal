package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.DomainName
import io.hamal.lib.common.domain.DomainNameSerializer
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.base.InputsSerializer
import io.hamal.lib.domain.vo.base.Map
import io.hamal.lib.kua.type.MapType
import kotlinx.serialization.Serializable

@Serializable(with = EndpointId.Serializer::class)
class EndpointId(override val value: SnowflakeId) : SerializableDomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))

    internal object Serializer : SerializableDomainIdSerializer<EndpointId>(::EndpointId)
}

@Serializable(with = EndpointName.Serializer::class)
class EndpointName(override val value: String) : DomainName() {
    internal object Serializer : DomainNameSerializer<EndpointName>(::EndpointName)
}

@Serializable(with = EndpointHeaders.Serializer::class)
class EndpointHeaders(override val value: MapType = MapType()) : Map() {
    internal object Serializer : InputsSerializer<EndpointHeaders>(::EndpointHeaders)
}

@Serializable(with = EndpointParameters.Serializer::class)
class EndpointParameters(override val value: MapType = MapType()) : Map() {
    internal object Serializer : InputsSerializer<EndpointParameters>(::EndpointParameters)
}

@Serializable(with = EndpointContent.Serializer::class)
class EndpointContent(override val value: MapType = MapType()) : Map() {
    internal object Serializer : InputsSerializer<EndpointContent>(::EndpointContent)
}
