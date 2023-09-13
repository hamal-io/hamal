package io.hamal.request

import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.CodeType

interface CreateFuncReq {
    val namespaceId: NamespaceId?
    val name: FuncName
    val inputs: FuncInputs
    val code: CodeType
}


interface UpdateFuncReq {
    val namespaceId: NamespaceId?
    val name: FuncName?
    val inputs: FuncInputs?
    val code: CodeType?
}


interface InvokeFuncReq {
    val correlationId: CorrelationId?
    val inputs: InvocationInputs?
}