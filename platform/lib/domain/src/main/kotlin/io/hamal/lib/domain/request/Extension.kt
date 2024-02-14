package io.hamal.lib.domain.request

import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.*

interface ExtensionCreateRequest {
    val name: ExtensionName
    val code: CodeValue
}

data class ExtensionCreateRequested(
    override val id: RequestId,
    override var status: RequestStatus,
    val workspaceId: WorkspaceId,
    val extensionId: ExtensionId,
    val name: ExtensionName,
    val codeId: CodeId,
    val code: CodeValue
) : Requested()


interface ExtensionUpdateRequest {
    val name: ExtensionName?
    val code: CodeValue?
}


data class ExtensionUpdateRequested(
    override val id: RequestId,
    override var status: RequestStatus,
    val workspaceId: WorkspaceId,
    val extensionId: ExtensionId,
    val name: ExtensionName?,
    val code: CodeValue?
) : Requested()

