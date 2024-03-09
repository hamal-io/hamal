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
    override val requestId: RequestId,
    override val requestedBy: AuthId,
    override var requestStatus: RequestStatus,
    val id: BlueprintId,
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
    override val requestId: RequestId,
    override val requestedBy: AuthId,
    override var requestStatus: RequestStatus,
    val id: BlueprintId,
    val name: BlueprintName?,
    val inputs: BlueprintInputs?,
    val value: CodeValue?,
    val description: BlueprintDescription?
) : Requested()