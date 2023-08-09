package io.hamal.lib.sdk.domain

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.DomainId
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import kotlinx.serialization.Serializable

@Serializable
data class ApiReqList(
    val reqs: List<ApiSubmittedReq>
)

@Serializable
sealed interface ApiSubmittedReq {
    val reqId: ReqId
    var status: ReqStatus
}

@Serializable
data class ApiDefaultSubmittedReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
) : ApiSubmittedReq


@Serializable
data class ApiSubmittedReqWithDomainId(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val id: SnowflakeId,
) : ApiSubmittedReq {
    fun <DOMAIN_ID : DomainId> id(block: SnowflakeId.() -> DOMAIN_ID): DOMAIN_ID {
        return block(id)
    }
}

