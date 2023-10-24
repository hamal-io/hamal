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
    val code: ExtensionCode
) {
    @Serializable
    data class ExtensionCode(
        val id: CodeId,
        val version: CodeVersion
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
    override val codeId: CodeId,
    override val codeVersion: CodeVersion
) : CreateExtensionReq

@Serializable
data class ApiUpdateExtensionReq(
    override val name: ExtensionName? = null,
    override val codeId: CodeId? = null,
    override val codeVersion: CodeVersion? = null
) : UpdateExtensionReq


interface ApiExtensionService {
    fun create(groupId: GroupId, createExtReq: ApiCreateExtensionReq): ApiSubmittedReqWithId
    fun get(extId: ExtensionId): ApiExtension
    fun list(groupId: GroupId): ApiExtensionList
    fun update(extId: ExtensionId, updateExtReq: ApiUpdateExtensionReq): ApiSubmittedReqWithId
}


internal class ApiExtensionServiceImpl(
    private val template: HttpTemplate
) : ApiExtensionService {
    override fun create(groupId: GroupId, createExtReq: ApiCreateExtensionReq): ApiSubmittedReqWithId =
        template.post("/v1/groups/{groupId}/extensions")
            .path("groupId", groupId)
            .body(createExtReq)
            .execute()
            .fold(ApiSubmittedReqWithId::class)

    override fun get(extId: ExtensionId): ApiExtension =
        template.get("/v1/extensions/{extId}")
            .path("extId", extId)
            .execute()
            .fold(ApiExtension::class)

    override fun list(groupId: GroupId): ApiExtensionList =
        template.get("/v1/extensions")
            .parameter("group_ids", groupId)
            .execute()
            .fold(ApiExtensionList::class)

    override fun update(extId: ExtensionId, updateExtReq: ApiUpdateExtensionReq): ApiSubmittedReqWithId =
        template.patch("/v1/extensions/{extId}/update")
            .path("extId", extId)
            .body(updateExtReq)
            .execute()
            .fold(ApiSubmittedReqWithId::class)
}