package io.hamal.request

import io.hamal.lib.domain.vo.FlowInputs
import io.hamal.lib.domain.vo.FlowName

interface CreateFlowReq {
    val name: FlowName
    val inputs: FlowInputs
}

interface UpdateFlowReq {
    val name: FlowName
    val inputs: FlowInputs
}
