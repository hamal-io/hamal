package io.hamal.lib.sdk.api

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.fold
import io.hamal.request.BlueprintCreateReq
import io.hamal.request.BlueprintUpdateReq

data class ApiBlueprint(
    val id: BlueprintId,
    val name: BlueprintName,
    val inputs: BlueprintInputs,
    val value: CodeValue
)

data class ApiBlueprintCreateReq(
    override val name: BlueprintName,
    override val inputs: BlueprintInputs,
    override val value: CodeValue
) : BlueprintCreateReq

data class ApiBlueprintCreateSubmitted(
    override val id: ReqId,
    override val status: ReqStatus,
    val blueprintId: BlueprintId,
    val groupId: GroupId,
) : ApiSubmitted

data class ApiBlueprintUpdateReq(
    override val name: BlueprintName? = null,
    override val inputs: BlueprintInputs? = null,
    override val value: CodeValue? = null
) : BlueprintUpdateReq

data class ApiBlueprintUpdateSubmitted(
    override val id: ReqId,
    override val status: ReqStatus,
    val blueprintId: BlueprintId
) : ApiSubmitted


interface ApiBlueprintService {
    fun create(groupId: GroupId, req: ApiBlueprintCreateReq): ApiBlueprintCreateSubmitted
    fun get(bpId: BlueprintId): ApiBlueprint
    fun update(bpId: BlueprintId, req: ApiBlueprintUpdateReq): ApiBlueprintUpdateSubmitted
}

internal class ApiBlueprintServiceImpl(
    private val template: HttpTemplate
) : ApiBlueprintService {

    override fun create(groupId: GroupId, req: ApiBlueprintCreateReq): ApiBlueprintCreateSubmitted =
        template.post("/v1/groups/{groupId}/blueprints")
            .path("groupId", groupId)
            .body(req)
            .execute()
            .fold(ApiBlueprintCreateSubmitted::class)


    override fun get(bpId: BlueprintId): ApiBlueprint =
        template.get("/v1/blueprints/{bpId}")
            .path("bpId", bpId)
            .execute()
            .fold(ApiBlueprint::class)


    override fun update(bpId: BlueprintId, req: ApiBlueprintUpdateReq): ApiBlueprintUpdateSubmitted =
        template.patch("/v1/blueprints/{bpId}")
            .path("bpId", bpId)
            .body(req)
            .execute()
            .fold(ApiBlueprintUpdateSubmitted::class)
}



