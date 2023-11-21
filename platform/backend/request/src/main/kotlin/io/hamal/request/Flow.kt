package io.hamal.request

import io.hamal.lib.domain.vo.FlowInputs
import io.hamal.lib.domain.vo.FlowName
import io.hamal.lib.domain.vo.FlowType

interface CreateFlowReq {
    val name: FlowName
    val inputs: FlowInputs
    val type: FlowType?
}

interface UpdateFlowReq {
    val name: FlowName
    val inputs: FlowInputs
}
