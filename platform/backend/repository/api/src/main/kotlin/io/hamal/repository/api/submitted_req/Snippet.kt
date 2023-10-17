package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@kotlinx.serialization.Serializable
data class SubmittedCreateSnippetReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    override val groupId: GroupId,
    val id: SnippetId,
    val name: SnippetName,
    val inputs: SnippetInputs,
    val value: CodeValue,
    val accountId: AccountId
) : SubmittedReqWithGroupId


@Serializable
data class SubmittedUpdateSnippetReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    override val groupId: GroupId,
    val id: SnippetId,
    val name: SnippetName?,
    val inputs: SnippetInputs?,
    val value: CodeValue?
) : SubmittedReqWithGroupId