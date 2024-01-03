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
        query: RequestQueryRepository.ReqQuery,
        responseHandler: (List<Requested>) -> T
    ): T
}

interface RequestPort : RequestGetPort, RequestListPort

@Component
class RequestAdapter(private val reqQueryRepository: RequestQueryRepository) : RequestPort {

    override fun <T : Any> invoke(reqId: RequestId, responseHandler: (Requested) -> T): T =
        responseHandler(reqQueryRepository.get(reqId))

    override operator fun <T : Any> invoke(
        query: RequestQueryRepository.ReqQuery,
        responseHandler: (List<Requested>) -> T
    ): T = responseHandler(reqQueryRepository.list(query))
}