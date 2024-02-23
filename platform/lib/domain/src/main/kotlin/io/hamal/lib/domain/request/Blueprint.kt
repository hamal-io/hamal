package io.hamal.lib.domain.request

import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.*

interface BlueprintCreateRequest {
    val name: BlueprintName
    val inputs: BlueprintInputs
    val value: CodeValue
    val description: BlueprintDescription?
}

data class BlueprintCreateRequested(
    override val id: RequestId,
    override val by: AuthId,
    override var status: RequestStatus,
    val blueprintId: BlueprintId,
    val creatorId: AccountId,
    val name: BlueprintName,
    val inputs: BlueprintInputs,
    val value: CodeValue,
    val description: BlueprintDescription?
) : Requested()


interface BlueprintUpdateRequest {
    val name: BlueprintName?
    val inputs: BlueprintInputs?
    val value: CodeValue?
    val description: BlueprintDescription?
}

data class BlueprintUpdateRequested(
    override val id: RequestId,
    override val by: AuthId,
    override var status: RequestStatus,
    val blueprintId: BlueprintId,
    val name: BlueprintName?,
    val inputs: BlueprintInputs?,
    val value: CodeValue?,
    val description: BlueprintDescription?
) : Requested()