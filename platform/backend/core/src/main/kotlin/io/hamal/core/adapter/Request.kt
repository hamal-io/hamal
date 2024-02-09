package io.hamal.core.adapter

import io.hamal.lib.domain.request.Requested
import io.hamal.lib.domain.vo.RequestId
import io.hamal.repository.api.RequestQueryRepository
import org.springframework.stereotype.Component


interface RequestGetPort {
    operator fun <T : Any> invoke(reqId: RequestId, responseHandler: (Requested) -> T): T
}

interface RequestListPort {
    operator fun <T : Any> invoke(
        query: RequestQueryRepository.RequestQuery,
        responseHandler: (List<Requested>) -> T
    ): T
}

interface RequestPort : RequestGetPort, RequestListPort

@Component
class RequestAdapter(private val requestQueryRepository: RequestQueryRepository) : RequestPort {

    override fun <T : Any> invoke(reqId: RequestId, responseHandler: (Requested) -> T): T =
        responseHandler(requestQueryRepository.get(reqId))

    override operator fun <T : Any> invoke(
        query: RequestQueryRepository.RequestQuery,
        responseHandler: (List<Requested>) -> T
    ): T = responseHandler(requestQueryRepository.list(query))
}