package io.hamal.core.adapter.request

import io.hamal.lib.domain.request.Requested
import io.hamal.lib.domain.vo.RequestId
import io.hamal.repository.api.RequestQueryRepository
import org.springframework.stereotype.Component

fun interface RequestGetPort {
    operator fun invoke(requestId: RequestId): Requested
}

@Component
class RequestGetAdapter(
    private val requestQueryRepository: RequestQueryRepository
) : RequestGetPort {
    override fun invoke(requestId: RequestId): Requested = requestQueryRepository.get(requestId)
}
