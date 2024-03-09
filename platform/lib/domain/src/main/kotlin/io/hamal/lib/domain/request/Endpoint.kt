package io.hamal.lib.domain.request

import io.hamal.lib.domain._enum.EndpointMethod
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.*

interface EndpointCreateRequest {
    val funcId: FuncId
    val name: EndpointName
    val method: EndpointMethod
}

data class EndpointCreateRequested(
    override val requestId: RequestId,
    override val requestedBy: AuthId,
    override var requestStatus: RequestStatus,
    val id: EndpointId,
    val funcId: FuncId,
    val workspaceId: WorkspaceId,
    val name: EndpointName,
    val method: EndpointMethod
) : Requested()

interface EndpointUpdateRequest {
    val funcId: FuncId?
    val name: EndpointName?
    val method: EndpointMethod?
}

data class EndpointUpdateRequested(
    override val requestId: RequestId,
    override val requestedBy: AuthId,
    override var requestStatus: RequestStatus,
    val id: EndpointId,
    val workspaceId: WorkspaceId,
    val funcId: FuncId,
    val name: EndpointName?,
    val method: EndpointMethod?
) : Requested()
