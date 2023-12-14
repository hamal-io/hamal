package io.hamal.request

import io.hamal.lib.domain._enum.EndpointMethod
import io.hamal.lib.domain.vo.EndpointName
import io.hamal.lib.domain.vo.FuncId

interface EndpointCreateReq {
    val funcId: FuncId
    val name: EndpointName
    val method: EndpointMethod
}

interface EndpointUpdateReq {
    val funcId: FuncId?
    val name: EndpointName?
    val method: EndpointMethod?
}