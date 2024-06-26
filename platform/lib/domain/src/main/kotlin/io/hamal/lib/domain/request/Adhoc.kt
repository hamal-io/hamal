package io.hamal.lib.domain.request

import io.hamal.lib.domain.vo.CodeType
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.InvocationInputs

interface AdhocInvokeRequest {
    val inputs: InvocationInputs?
    val code: CodeValue
    val codeType: CodeType
}