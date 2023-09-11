package io.hamal.request.adhoc

import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.kua.type.CodeType

interface InvokeAdhocReq {
    val inputs: InvocationInputs
    val code: CodeType
}