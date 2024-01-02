package io.hamal.lib.sdk.api

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.ReqId

data class ApiReqList(
    val reqs: List<ApiSubmitted>
)

sealed interface ApiSubmitted {
    val id: ReqId
    val status: ReqStatus
}
