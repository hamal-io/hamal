package io.hamal.lib.sdk.api

import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.ExtensionCreateRequest
import io.hamal.lib.domain.request.ExtensionUpdateRequest
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.fold

data class ApiExtension(
    val id: ExtensionId,
    val name: ExtensionName,
    val code: Code
) : ApiObject() {
    data class Code(
        val id: CodeId,
        val version: CodeVersion,
        val value: CodeValue
    )
}

data class ApiExtensionList(
    val extensions: List<Extension>
) : ApiObject() {
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
    val workspaceId: WorkspaceId
) : ApiRequested()

data class ApiExtensionUpdateRequest(
    override val name: ExtensionName? = null,
    override val code: CodeValue? = null
) : ExtensionUpdateRequest

data class ApiExtensionUpdateRequested(
    override val id: RequestId,
    override val status: RequestStatus,
    val extensionId: ExtensionId
) : ApiRequested()


interface ApiExtensionService {
    fun create(workspaceId: WorkspaceId, req: ApiExtensionCreateRequest): ApiExtensionCreateRequested
    fun get(extensionId: ExtensionId): ApiExtension
    fun list(workspaceId: WorkspaceId): List<ApiExtensionList.Extension>
    fun update(extensionId: ExtensionId, req: ApiExtensionUpdateRequest): ApiExtensionUpdateRequested
}


internal class ApiExtensionServiceImpl(
    private val template: HttpTemplate
) : ApiExtensionService {
    override fun create(workspaceId: WorkspaceId, req: ApiExtensionCreateRequest): ApiExtensionCreateRequested =
        template.post("/v1/workspaces/{workspaceId}/extensions")
            .path("workspaceId", workspaceId)
            .body(req)
            .execute()
            .fold(ApiExtensionCreateRequested::class)

    override fun get(extensionId: ExtensionId): ApiExtension =
        template.get("/v1/extensions/{extensionId}")
            .path("extensionId", extensionId)
            .execute()
            .fold(ApiExtension::class)

    override fun list(workspaceId: WorkspaceId): List<ApiExtensionList.Extension> =
        template.get("/v1/extensions")
            .parameter("workspace_ids", workspaceId)
            .execute()
            .fold(ApiExtensionList::class)
            .extensions

    override fun update(extensionId: ExtensionId, req: ApiExtensionUpdateRequest): ApiExtensionUpdateRequested =
        template.patch("/v1/extensions/{extensionId}")
            .path("extensionId", extensionId)
            .body(req)
            .execute()
            .fold(ApiExtensionUpdateRequested::class)

}