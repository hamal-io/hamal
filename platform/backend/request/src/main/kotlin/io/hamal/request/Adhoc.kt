package io.hamal.request

import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.InvocationInputs

interface InvokeAdhocReq {
    val inputs: InvocationInputs
    val code: CodeValue
}