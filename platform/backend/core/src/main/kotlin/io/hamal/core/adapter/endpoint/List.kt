package io.hamal.core.adapter.endpoint

import io.hamal.core.adapter.security.FilterAccessPort
import io.hamal.repository.api.Endpoint
import io.hamal.repository.api.EndpointQueryRepository
import io.hamal.repository.api.EndpointQueryRepository.EndpointQuery
import org.springframework.stereotype.Component

fun interface EndpointListPort {
    operator fun invoke(query: EndpointQuery): List<Endpoint>
}

@Component
class EndpointListAdapter(
    private val endpointQueryRepository: EndpointQueryRepository,
    private val filterAccess: FilterAccessPort
) : EndpointListPort {
    override fun invoke(query: EndpointQuery): List<Endpoint> = filterAccess(endpointQueryRepository.list(query))
}