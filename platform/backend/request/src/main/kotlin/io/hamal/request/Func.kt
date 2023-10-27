package io.hamal.request

import io.hamal.lib.domain.vo.*


interface CreateFuncReq {
    val name: FuncName
    val inputs: FuncInputs
    val code: CodeValue
}


interface UpdateFuncReq {
    val name: FuncName?
    val inputs: FuncInputs?
    val code: CodeValue?
}


interface InvokeFuncReq {
    val correlationId: CorrelationId?
    val inputs: InvocationInputs?
}