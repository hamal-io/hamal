package io.hamal.request

import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.InvocationInputs

interface AdhocInvokeReq {
    val inputs: InvocationInputs
    val code: CodeValue
}