package io.hamal.lib.sdk.api

import io.hamal.lib.common.domain.DomainId
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.AuthToken
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.NamespaceId
import kotlinx.serialization.Serializable

@Serializable
data class ApiReqList(
    val reqs: List<ApiSubmittedReq>
)

@Serializable
data class BetterApiSubmittedReq<ID : DomainId>(
    val reqId: ReqId,
    val status: ReqStatus,
    val id: ID,
    val namespaceId: NamespaceId? = null,
    val groupId: GroupId? = null,
    val token: AuthToken? = null
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
    val namespaceId: NamespaceId
    val groupId: GroupId
}

@Serializable
data class ApiDefaultSubmittedReq(
    override val reqId: ReqId,
    override val status: ReqStatus,
    override val namespaceId: NamespaceId,
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
    override val namespaceId: NamespaceId,
    override val groupId: GroupId,
    val id: SnowflakeId,
) : ApiSubmittedReqWithGroupId {

    constructor(reqId: ReqId, status: ReqStatus, namespaceId: NamespaceId, groupId: GroupId, id: DomainId) : this(
        reqId,
        status,
        namespaceId,
        groupId,
        id.value,
    )

    fun <DOMAIN_ID : DomainId> id(block: SnowflakeId.() -> DOMAIN_ID): DOMAIN_ID {
        return block(id)
    }
}

