package io.hamal.lib.sdk.api

import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.domain.vo.RequestType

data class ApiRequestList(
    val requests: List<ApiRequested>
)

sealed class ApiRequested {
    abstract val id: RequestId
    abstract val status: RequestStatus
    val type: RequestType = RequestType(this::class.java.simpleName)
}
