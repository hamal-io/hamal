package io.hamal.lib.sdk.api

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
data class ApiReqList(
    val reqs: List<Submitted>
) {
    @Serializable
    data class Submitted(
        override val reqId: ReqId,
        override val status: ReqStatus,
    ) : ApiSubmittedReq
}

@Serializable
data class ApiSubmittedSimpleReq(
    val reqId: ReqId,
    val status: ReqStatus
)


@Serializable
sealed interface ApiSubmittedReq {
    val reqId: ReqId
    val status: ReqStatus
}

@Serializable
data class ApiSubmittedReqImpl<ID : SerializableDomainId>(
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
