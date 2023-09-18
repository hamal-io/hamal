package io.hamal.lib.sdk.admin

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.DomainId
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.AuthToken
import io.hamal.lib.domain.vo.GroupId
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
sealed interface AdminSubmittedReqWithGroupId : AdminSubmittedReq {
    override val reqId: ReqId
    override val status: ReqStatus
    val groupId: GroupId
}


@Serializable
data class AdminDefaultSubmittedReq(
    override val reqId: ReqId,
    override val status: ReqStatus,
    override val groupId: GroupId
) : AdminSubmittedReqWithGroupId

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
    override val groupId: GroupId,
    val id: SnowflakeId,
) : AdminSubmittedReqWithGroupId {

    constructor(reqId: ReqId, status: ReqStatus, groupId: GroupId, id: DomainId) : this(
        reqId,
        status,
        groupId,
        id.value
    )

    fun <DOMAIN_ID : DomainId> id(block: SnowflakeId.() -> DOMAIN_ID): DOMAIN_ID {
        return block(id)
    }
}

