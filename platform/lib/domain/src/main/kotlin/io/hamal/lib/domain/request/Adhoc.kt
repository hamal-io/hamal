package io.hamal.lib.domain.request

import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.domain.vo.CodeType
import io.hamal.lib.domain.vo.InvocationInputs

interface AdhocInvokeRequest {
    val inputs: InvocationInputs?
    val code: ValueCode
    val codeType: CodeType
}