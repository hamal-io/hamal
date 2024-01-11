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
    override val id: RequestId,
    override var status: RequestStatus,
    val endpointId: EndpointId,
    val funcId: FuncId,
    val groupId: GroupId,
    val name: EndpointName,
    val method: EndpointMethod
) : Requested()

interface EndpointUpdateRequest {
    val funcId: FuncId?
    val name: EndpointName?
    val method: EndpointMethod?
}

data class EndpointUpdateRequested(
    override val id: RequestId,
    override var status: RequestStatus,
    val endpointId: EndpointId,
    val groupId: GroupId,
    val funcId: FuncId,
    val name: EndpointName?,
    val method: EndpointMethod?
) : Requested()
