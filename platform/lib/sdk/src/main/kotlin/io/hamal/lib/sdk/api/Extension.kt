package io.hamal.lib.sdk.api

import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.fold
import io.hamal.request.CreateExtensionReq
import io.hamal.request.UpdateExtensionReq
import kotlinx.serialization.Serializable

@Serializable
data class ApiExtension(
    val id: ExtensionId,
    val name: ExtensionName,
    val code: Code
) {
    @Serializable
    data class Code(
        val id: CodeId,
        val version: CodeVersion,
        val value: CodeValue
    )
}

@Serializable
data class ApiExtensionList(
    val extensions: List<Extension>
) {
    @Serializable
    data class Extension(
        val id: ExtensionId,
        val name: ExtensionName,
    )
}

@Serializable
data class ApiCreateExtensionReq(
    override val name: ExtensionName,
    override val code: CodeValue

) : CreateExtensionReq

@Serializable
data class ApiUpdateExtensionReq(
    override val name: ExtensionName? = null,
    override val code: CodeValue? = null
) : UpdateExtensionReq


interface ApiExtensionService {
    fun create(groupId: GroupId, req: ApiCreateExtensionReq): ApiSubmittedReqWithId
    fun get(extId: ExtensionId): ApiExtension
    fun list(groupId: GroupId): List<ApiExtensionList.Extension>
    fun update(extId: ExtensionId, req: ApiUpdateExtensionReq): ApiSubmittedReqWithId
}


internal class ApiExtensionServiceImpl(
    private val template: HttpTemplate
) : ApiExtensionService {
    override fun create(groupId: GroupId, req: ApiCreateExtensionReq): ApiSubmittedReqWithId =
        template.post("/v1/groups/{groupId}/extensions")
            .path("groupId", groupId)
            .body(req)
            .execute()
            .fold(ApiSubmittedReqWithId::class)

    override fun get(extId: ExtensionId): ApiExtension =
        template.get("/v1/extensions/{extId}")
            .path("extId", extId)
            .execute()
            .fold(ApiExtension::class)

    override fun list(groupId: GroupId): List<ApiExtensionList.Extension> =
        template.get("/v1/extensions")
            .parameter("group_ids", groupId)
            .execute()
            .fold(ApiExtensionList::class)
            .extensions

    override fun update(extId: ExtensionId, req: ApiUpdateExtensionReq): ApiSubmittedReqWithId =
        template.patch("/v1/extensions/{extId}/update")
            .path("extId", extId)
            .body(req)
            .execute()
            .fold(ApiSubmittedReqWithId::class)
}