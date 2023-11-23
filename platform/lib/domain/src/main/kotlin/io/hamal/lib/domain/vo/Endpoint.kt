package io.hamal.lib.domain.vo

import io.hamal.lib.domain.vo.base.InputsSerializer
import io.hamal.lib.domain.vo.base.Map
import io.hamal.lib.kua.type.MapType
import kotlinx.serialization.Serializable

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
