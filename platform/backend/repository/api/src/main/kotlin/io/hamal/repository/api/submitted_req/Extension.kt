package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
data class ExtensionCreateSubmitted(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val groupId: GroupId,
    val id: ExtensionId,
    val name: ExtensionName,
    val codeId: CodeId,
    val code: CodeValue
) : Submitted


@Serializable
data class ExtensionUpdateSubmitted(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val groupId: GroupId,
    val id: ExtensionId,
    val name: ExtensionName?,
    val code: CodeValue?
) : Submitted

