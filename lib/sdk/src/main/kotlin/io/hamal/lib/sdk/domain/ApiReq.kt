package io.hamal.lib.sdk.domain

import io.hamal.lib.domain.ComputeId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiListReqResponse(
    val reqs: List<Req>
) {
    @Serializable
    data class Req(
        val id: ComputeId,
        val status: ReqStatus
    )
}


@Serializable
sealed interface ApiReq {
    val id: ComputeId
    val status: ReqStatus
}

@Serializable
@SerialName("Adhoc")
data class ApiAdhocRequest(
    override val id: ComputeId,
    override val status: ReqStatus,
    val execId: ExecId?,
    val execStatus: ExecStatus?
) : ApiReq

