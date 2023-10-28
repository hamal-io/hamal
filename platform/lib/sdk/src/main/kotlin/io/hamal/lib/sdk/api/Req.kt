package io.hamal.lib.sdk.api

import io.hamal.lib.common.domain.DomainId
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
sealed interface ApiSubmittedReq {
    val reqId: ReqId
    val status: ReqStatus
}

@Serializable
data class ApiSubmittedReqImpl<ID : DomainId>(
    override val reqId: ReqId,
    override val status: ReqStatus,
    val id: ID,
    val namespaceId: NamespaceId? = null,
    val groupId: GroupId? = null,
) : ApiSubmittedReq

@Serializable
data class ApiSubmittedWithTokenReq(
    override val reqId: ReqId,
    override val status: ReqStatus,
    val token: AuthToken
) : ApiSubmittedReq
