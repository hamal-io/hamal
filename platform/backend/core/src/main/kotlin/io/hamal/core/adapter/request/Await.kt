package io.hamal.core.adapter.request

import io.hamal.lib.domain._enum.RequestStatuses.Completed
import io.hamal.lib.domain._enum.RequestStatuses.Failed
import io.hamal.lib.domain.request.Requested
import io.hamal.lib.domain.vo.RequestStatus.Companion.RequestStatus
import io.hamal.repository.api.RequestQueryRepository
import org.springframework.stereotype.Component


interface RequestAwaitPort {
    operator fun invoke(requested: Requested)
}

@Component
class RequestAwaitAdapter(
    private val requestQueryRepository: RequestQueryRepository
) : RequestAwaitPort {
    override fun invoke(requested: Requested) {
        while (true) {
            val currentStatus = requestQueryRepository.get(requested.requestId).requestStatus
            if (currentStatus in setOf(RequestStatus(Completed), RequestStatus(Failed))) {
                break
            }
            Thread.sleep(100)
        }
    }
}