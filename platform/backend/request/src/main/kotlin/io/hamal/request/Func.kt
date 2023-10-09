package io.hamal.request

import io.hamal.lib.domain.vo.*


interface CreateFuncReq {
    val namespaceId: NamespaceId?
    val name: FuncName
    val inputs: FuncInputs
    val code: CodeValue
    val codeType: CodeType
}


interface UpdateFuncReq {
    val namespaceId: NamespaceId?
    val name: FuncName?
    val inputs: FuncInputs?
    val code: CodeValue?
}


interface InvokeFuncReq {
    val correlationId: CorrelationId?
    val inputs: InvocationInputs?
}