package io.hamal.lib.sdk.hub.domain

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.DomainId
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.AuthToken
import kotlinx.serialization.Serializable

@Serializable
data class ApiReqList(
    val reqs: List<ApiSubmittedReq>
)

@Serializable
sealed interface ApiSubmittedReq {
    val reqId: ReqId
    val status: ReqStatus
}

@Serializable
data class ApiDefaultSubmittedReq(
    override val reqId: ReqId,
    override val status: ReqStatus,
) : ApiSubmittedReq

@Serializable
data class ApiSubmittedWithTokenReq(
    override val reqId: ReqId,
    override val status: ReqStatus,
    val token: AuthToken
) : ApiSubmittedReq

@Serializable
data class ApiSubmittedReqWithId(
    override val reqId: ReqId,
    override val status: ReqStatus,
    val id: SnowflakeId,
) : ApiSubmittedReq {

    constructor(reqId: ReqId, status: ReqStatus, id: DomainId) : this(reqId, status, id.value)

    fun <DOMAIN_ID : DomainId> id(block: SnowflakeId.() -> DOMAIN_ID): DOMAIN_ID {
        return block(id)
    }
}

