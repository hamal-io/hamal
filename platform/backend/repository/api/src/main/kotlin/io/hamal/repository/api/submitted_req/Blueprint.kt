package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*

data class BlueprintCreateSubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val groupId: GroupId,
    val blueprintId: BlueprintId,
    val creatorId: AccountId,
    val name: BlueprintName,
    val inputs: BlueprintInputs,
    val value: CodeValue
) : Submitted


data class BlueprintUpdateSubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val groupId: GroupId,
    val blueprintId: BlueprintId,
    val name: BlueprintName?,
    val inputs: BlueprintInputs?,
    val value: CodeValue?
) : Submitted