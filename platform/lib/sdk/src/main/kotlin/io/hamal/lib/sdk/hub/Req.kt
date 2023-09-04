package io.hamal.lib.sdk.hub

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.DomainId
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.AuthToken
import kotlinx.serialization.Serializable

@Serializable
data class HubReqList(
    val reqs: List<HubSubmittedReq>
)

@Serializable
sealed interface HubSubmittedReq {
    val reqId: ReqId
    val status: ReqStatus
}

@Serializable
data class HubDefaultSubmittedReq(
    override val reqId: ReqId,
    override val status: ReqStatus,
) : HubSubmittedReq

@Serializable
data class HubSubmittedWithTokenReq(
    override val reqId: ReqId,
    override val status: ReqStatus,
    val token: AuthToken
) : HubSubmittedReq

@Serializable
data class HubSubmittedReqWithId(
    override val reqId: ReqId,
    override val status: ReqStatus,
    val id: SnowflakeId,
) : HubSubmittedReq {

    constructor(reqId: ReqId, status: ReqStatus, id: DomainId) : this(reqId, status, id.value)

    fun <DOMAIN_ID : DomainId> id(block: SnowflakeId.() -> DOMAIN_ID): DOMAIN_ID {
        return block(id)
    }
}

