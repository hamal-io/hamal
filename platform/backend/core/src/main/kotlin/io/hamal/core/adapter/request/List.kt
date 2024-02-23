package io.hamal.core.adapter.request

import io.hamal.lib.domain.request.Requested
import io.hamal.repository.api.RequestQueryRepository
import io.hamal.repository.api.RequestQueryRepository.RequestQuery
import org.springframework.stereotype.Component

fun interface RequestListPort {
    operator fun invoke(query: RequestQuery): List<Requested>
}

@Component
class RequestListAdapter(
    private val requestQueryRepository: RequestQueryRepository
) : RequestListPort {
    override fun invoke(query: RequestQuery): List<Requested> = requestQueryRepository.list(query)
}
