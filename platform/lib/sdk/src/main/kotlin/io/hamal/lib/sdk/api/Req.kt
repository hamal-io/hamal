package io.hamal.lib.sdk.api

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.ReqId
import kotlinx.serialization.Serializable

@Serializable
data class ApiReqList(
    val reqs: List<ApiSubmitted>
)

@Serializable
sealed interface ApiSubmitted {
    val id: ReqId
    val status: ReqStatus
}
