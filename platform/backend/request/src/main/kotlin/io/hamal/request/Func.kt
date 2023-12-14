package io.hamal.request

import io.hamal.lib.domain.vo.*


interface FuncCreateReq {
    val name: FuncName
    val inputs: FuncInputs
    val code: CodeValue
}

interface FuncUpdateReq {
    val name: FuncName?
    val inputs: FuncInputs?
    val code: CodeValue?
}

interface FuncInvokeReq {
    val correlationId: CorrelationId?
    val inputs: InvocationInputs
    val invocation: Invocation
}

interface FuncInvokeVersionReq {
    val correlationId: CorrelationId?
    val inputs: InvocationInputs
    val version: CodeVersion?
}

interface FuncDeployReq {
    val version: CodeVersion?
    val message: DeployMessage?
}