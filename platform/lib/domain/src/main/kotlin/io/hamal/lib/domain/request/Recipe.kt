package io.hamal.lib.domain.request

import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.*

interface RecipeCreateRequest {
    val name: RecipeName
    val inputs: RecipeInputs
    val value: ValueCode
    val description: RecipeDescription?
}

data class RecipeCreateRequested(
    override val requestId: RequestId,
    override val requestedBy: AuthId,
    override var requestStatus: RequestStatus,
    val id: RecipeId,
    val creatorId: AccountId,
    val name: RecipeName,
    val inputs: RecipeInputs,
    val value: ValueCode,
    val description: RecipeDescription?
) : Requested()


interface RecipeUpdateRequest {
    val name: RecipeName?
    val inputs: RecipeInputs?
    val value: ValueCode?
    val description: RecipeDescription?
}

data class RecipeUpdateRequested(
    override val requestId: RequestId,
    override val requestedBy: AuthId,
    override var requestStatus: RequestStatus,
    val id: RecipeId,
    val name: RecipeName?,
    val inputs: RecipeInputs?,
    val value: ValueCode?,
    val description: RecipeDescription?
) : Requested()