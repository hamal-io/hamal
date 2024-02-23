package io.hamal.core.adapter.request

import io.hamal.lib.domain.request.Requested
import io.hamal.repository.api.RequestCmdRepository
import org.springframework.stereotype.Component

interface RequestEnqueuePort {
    operator fun invoke(requested: Requested)
}

@Component
class RequestQueueAdapter(
    private val requestCmdRepository: RequestCmdRepository
) : RequestEnqueuePort {
    override fun invoke(requested: Requested) {
        requestCmdRepository.queue(requested)
    }
}