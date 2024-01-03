package io.hamal.lib.sdk.api

import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.fold
import io.hamal.lib.domain.request.ExtensionCreateRequest
import io.hamal.lib.domain.request.ExtensionUpdateRequest

data class ApiExtension(
    val id: ExtensionId,
    val name: ExtensionName,
    val code: Code
) {
    data class Code(
        val id: CodeId,
        val version: CodeVersion,
        val value: CodeValue
    )
}

data class ApiExtensionList(
    val extensions: List<Extension>
) {
    data class Extension(
        val id: ExtensionId,
        val name: ExtensionName,
    )
}

data class ApiExtensionCreateRequest(
    override val name: ExtensionName,
    override val code: CodeValue
) : ExtensionCreateRequest

data class ApiExtensionCreateRequested(
    override val id: RequestId,
    override val status: RequestStatus,
    val extensionId: ExtensionId,
    val groupId: GroupId
) : ApiRequested

data class ApiExtensionUpdateRequest(
    override val name: ExtensionName? = null,
    override val code: CodeValue? = null
) : ExtensionUpdateRequest

data class ApiExtensionUpdateRequested(
    override val id: RequestId,
    override val status: RequestStatus,
    val extensionId: ExtensionId
) : ApiRequested


interface ApiExtensionService {
    fun create(groupId: GroupId, req: ApiExtensionCreateRequest): ApiExtensionCreateRequested
    fun get(extId: ExtensionId): ApiExtension
    fun list(groupId: GroupId): List<ApiExtensionList.Extension>
    fun update(extId: ExtensionId, req: ApiExtensionUpdateRequest): ApiExtensionUpdateRequested
}


internal class ApiExtensionServiceImpl(
    private val template: HttpTemplate
) : ApiExtensionService {
    override fun create(groupId: GroupId, req: ApiExtensionCreateRequest): ApiExtensionCreateRequested =
        template.post("/v1/groups/{groupId}/extensions")
            .path("groupId", groupId)
            .body(req)
            .execute()
            .fold(ApiExtensionCreateRequested::class)

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

    override fun update(extId: ExtensionId, req: ApiExtensionUpdateRequest): ApiExtensionUpdateRequested =
        template.patch("/v1/extensions/{extId}")
            .path("extId", extId)
            .body(req)
            .execute()
            .fold(ApiExtensionUpdateRequested::class)

}