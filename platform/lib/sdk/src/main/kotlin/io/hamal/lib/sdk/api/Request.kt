package io.hamal.lib.sdk.api

import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.RequestId

data class ApiRequestList(
    val reqs: List<ApiRequested>
)

sealed interface ApiRequested {
    val id: RequestId
    val status: RequestStatus
}
