package io.hamal.lib.sdk.domain

import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.req.ReqStatus
import io.hamal.lib.domain.req.SubmittedReq
import io.hamal.lib.domain.vo.ExecId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ListSubmittedReqsResponse(
    val reqs: List<SubmittedReq>
)


@Serializable
@Deprecated("do not have separate dto")
sealed interface ApiReq {
    val id: ReqId
    val status: ReqStatus
}

@Serializable
@SerialName("AdhocInvocation")
@Deprecated("do not have separate dto")
data class ApiAdhocReq(
    override val id: ReqId,
    override val status: ReqStatus,
    val execId: ExecId?,
) : ApiReq

