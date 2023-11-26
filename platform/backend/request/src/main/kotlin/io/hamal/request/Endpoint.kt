package io.hamal.request

import io.hamal.lib.domain._enum.EndpointMethod
import io.hamal.lib.domain.vo.EndpointName
import io.hamal.lib.domain.vo.FuncId

interface CreateEndpointReq {
    val funcId: FuncId
    val name: EndpointName
    val method: EndpointMethod
}

interface UpdateEndpointReq {
    val funcId: FuncId?
    val name: EndpointName?
    val method: EndpointMethod?
}