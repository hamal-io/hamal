package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
data class SnippetCreateSubmitted(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val groupId: GroupId,
    val id: SnippetId,
    val creatorId: AccountId,
    val name: SnippetName,
    val inputs: SnippetInputs,
    val value: CodeValue
) : Submitted


@Serializable
data class SnippetUpdateSubmitted(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val groupId: GroupId,
    val id: SnippetId,
    val name: SnippetName?,
    val inputs: SnippetInputs?,
    val value: CodeValue?
) : Submitted