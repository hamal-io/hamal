package io.hamal.core.adapter.endpoint

import io.hamal.lib.domain.vo.EndpointId
import io.hamal.repository.api.Endpoint
import io.hamal.repository.api.EndpointQueryRepository
import org.springframework.stereotype.Component

fun interface EndpointGetPort {
    operator fun invoke(endpointId: EndpointId): Endpoint
}

@Component
class EndpointGetAdapter(
    private val endpointQueryRepository: EndpointQueryRepository
) : EndpointGetPort {
    override fun invoke(endpointId: EndpointId): Endpoint = endpointQueryRepository.get(endpointId)
}
