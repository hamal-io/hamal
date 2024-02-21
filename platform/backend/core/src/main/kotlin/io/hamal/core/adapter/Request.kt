package io.hamal.core.adapter

import io.hamal.lib.domain.request.Requested
import io.hamal.lib.domain.vo.RequestId
import io.hamal.repository.api.RequestCmdRepository
import io.hamal.repository.api.RequestQueryRepository
import org.springframework.stereotype.Component


interface RequestGetPort {
    operator fun invoke(reqId: RequestId): Requested
}

interface RequestListPort {
    operator fun invoke(query: RequestQueryRepository.RequestQuery): List<Requested>
}

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

interface RequestPort : RequestGetPort, RequestListPort

@Component
class RequestAdapter(private val requestQueryRepository: RequestQueryRepository) : RequestPort {

    override fun invoke(reqId: RequestId): Requested = requestQueryRepository.get(reqId)

    override operator fun invoke(query: RequestQueryRepository.RequestQuery): (List<Requested>) =
        requestQueryRepository.list(query)
}