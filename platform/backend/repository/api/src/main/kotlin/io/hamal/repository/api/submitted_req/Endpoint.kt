package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain._enum.EndpointMethod
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
data class EndpointCreateSubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val endpointId: EndpointId,
    val funcId: FuncId,
    val groupId: GroupId,
    val name: EndpointName,
    val method: EndpointMethod
) : Submitted

@Serializable
data class EndpointUpdateSubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val endpointId: EndpointId,
    val groupId: GroupId,
    val name: EndpointName?,
    val method: EndpointMethod?
) : Submitted
