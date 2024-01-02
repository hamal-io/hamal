package io.hamal.lib.domain.submitted

import io.hamal.lib.domain._enum.EndpointMethod
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*

data class EndpointCreateSubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val endpointId: EndpointId,
    val funcId: FuncId,
    val groupId: GroupId,
    val name: EndpointName,
    val method: EndpointMethod
) : Submitted()

data class EndpointUpdateSubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val endpointId: EndpointId,
    val groupId: GroupId,
    val funcId: FuncId,
    val name: EndpointName?,
    val method: EndpointMethod?
) : Submitted()
