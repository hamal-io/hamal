package io.hamal.lib.sdk.api

import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.BlueprintCreateRequest
import io.hamal.lib.domain.request.BlueprintUpdateRequest
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.fold

data class ApiBlueprint(
    val id: BlueprintId,
    val name: BlueprintName,
    val inputs: BlueprintInputs,
    val value: CodeValue
) : ApiObject()

data class ApiBlueprintCreateRequest(
    override val name: BlueprintName,
    override val inputs: BlueprintInputs,
    override val value: CodeValue
) : BlueprintCreateRequest

data class ApiBlueprintCreateRequested(
    override val id: RequestId,
    override val status: RequestStatus,
    val blueprintId: BlueprintId,
    val groupId: GroupId,
) : ApiRequested()

data class ApiBlueprintUpdateRequest(
    override val name: BlueprintName? = null,
    override val inputs: BlueprintInputs? = null,
    override val value: CodeValue? = null
) : BlueprintUpdateRequest

data class ApiBlueprintUpdateRequested(
    override val id: RequestId,
    override val status: RequestStatus,
    val blueprintId: BlueprintId
) : ApiRequested()


interface ApiBlueprintService {
    fun create(groupId: GroupId, req: ApiBlueprintCreateRequest): ApiBlueprintCreateRequested
    fun get(bpId: BlueprintId): ApiBlueprint
    fun update(bpId: BlueprintId, req: ApiBlueprintUpdateRequest): ApiBlueprintUpdateRequested
}

internal class ApiBlueprintServiceImpl(
    private val template: HttpTemplate
) : ApiBlueprintService {

    override fun create(groupId: GroupId, req: ApiBlueprintCreateRequest): ApiBlueprintCreateRequested =
        template.post("/v1/groups/{groupId}/blueprints")
            .path("groupId", groupId)
            .body(req)
            .execute()
            .fold(ApiBlueprintCreateRequested::class)


    override fun get(bpId: BlueprintId): ApiBlueprint =
        template.get("/v1/blueprints/{bpId}")
            .path("bpId", bpId)
            .execute()
            .fold(ApiBlueprint::class)


    override fun update(bpId: BlueprintId, req: ApiBlueprintUpdateRequest): ApiBlueprintUpdateRequested =
        template.patch("/v1/blueprints/{bpId}")
            .path("bpId", bpId)
            .body(req)
            .execute()
            .fold(ApiBlueprintUpdateRequested::class)
}



