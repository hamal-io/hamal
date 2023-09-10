package io.hamal.lib.sdk.admin

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.DomainId
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.AuthToken
import kotlinx.serialization.Serializable

@Serializable
data class AdminReqList(
    val reqs: List<AdminSubmittedReq>
)

@Serializable
sealed interface AdminSubmittedReq {
    val reqId: ReqId
    val status: ReqStatus
}

@Serializable
data class AdminDefaultSubmittedReq(
    override val reqId: ReqId,
    override val status: ReqStatus,
) : AdminSubmittedReq

@Serializable
data class AdminSubmittedWithTokenReq(
    override val reqId: ReqId,
    override val status: ReqStatus,
    val token: AuthToken
) : AdminSubmittedReq

@Serializable
data class AdminSubmittedReqWithId(
    override val reqId: ReqId,
    override val status: ReqStatus,
    val id: SnowflakeId,
) : AdminSubmittedReq {

    constructor(reqId: ReqId, status: ReqStatus, id: DomainId) : this(reqId, status, id.value)

    fun <DOMAIN_ID : DomainId> id(block: SnowflakeId.() -> DOMAIN_ID): DOMAIN_ID {
        return block(id)
    }
}

