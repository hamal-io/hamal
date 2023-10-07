package io.hamal.lib.sdk.api

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.DomainId
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.AuthToken
import io.hamal.lib.domain.vo.GroupId
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
sealed interface ApiSubmittedReqWithGroupId : ApiSubmittedReq {
    override val reqId: ReqId
    override val status: ReqStatus
    val groupId: GroupId
}

@Serializable
data class ApiDefaultSubmittedReq(
    override val reqId: ReqId,
    override val status: ReqStatus,
    override val groupId: GroupId
) : ApiSubmittedReqWithGroupId

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
    override val groupId: GroupId,
    val id: SnowflakeId,
) : ApiSubmittedReqWithGroupId {

    constructor(reqId: ReqId, status: ReqStatus, groupId: GroupId, id: DomainId) : this(
        reqId,
        status,
        groupId,
        id.value,
    )

    fun <DOMAIN_ID : DomainId> id(block: SnowflakeId.() -> DOMAIN_ID): DOMAIN_ID {
        return block(id)
    }
}

