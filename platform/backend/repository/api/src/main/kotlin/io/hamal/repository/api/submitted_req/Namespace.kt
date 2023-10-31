package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable


@Serializable
data class NamespaceCreateSubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val groupId: GroupId,
    val namespaceId: NamespaceId,
    val name: NamespaceName,
    val inputs: NamespaceInputs
) : Submitted


@Serializable
data class NamespaceUpdateSubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val groupId: GroupId,
    val namespaceId: NamespaceId,
    val name: NamespaceName,
    val inputs: NamespaceInputs
) : Submitted
