package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceInputs
import io.hamal.lib.domain.vo.NamespaceName
import kotlinx.serialization.Serializable


@Serializable
data class SubmittedCreateNamespaceReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val id: NamespaceId,
    val groupId: GroupId,
    val name: NamespaceName,
    val inputs: NamespaceInputs
) : SubmittedReq


@Serializable
data class SubmittedUpdateNamespaceReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val id: NamespaceId,
    val name: NamespaceName,
    val inputs: NamespaceInputs
) : SubmittedReq
