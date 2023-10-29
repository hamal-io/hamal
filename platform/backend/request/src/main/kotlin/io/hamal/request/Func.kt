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

abstract class DeployCodeReq : UpdateFuncReq {
    override val name: FuncName? = null
    override val inputs: FuncInputs? = null
    override val code: CodeValue? = null
    abstract val deployedVersion: CodeVersion
}


interface InvokeFuncReq {
    val correlationId: CorrelationId?
    val inputs: InvocationInputs?
}