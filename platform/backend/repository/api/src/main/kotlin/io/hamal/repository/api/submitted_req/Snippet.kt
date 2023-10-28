package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
data class SnippetCreateSubmittedReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    override val groupId: GroupId,
    val id: SnippetId,
    val creatorId: AccountId,
    val name: SnippetName,
    val inputs: SnippetInputs,
    val value: CodeValue
) : SubmittedReqWithGroupId


@Serializable
data class SnippetUpdateSubmittedReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    override val groupId: GroupId,
    val id: SnippetId,
    val name: SnippetName?,
    val inputs: SnippetInputs?,
    val value: CodeValue?
) : SubmittedReqWithGroupId