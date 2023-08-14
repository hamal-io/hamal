package io.hamal.lib.domain.req

import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.CodeType
import kotlinx.serialization.Serializable

@Serializable
data class CreateFuncReq(
    val namespaceId: NamespaceId?,
    val name: FuncName,
    val inputs: FuncInputs,
    val code: CodeType
)


@Serializable
data class UpdateFuncReq(
    val namespaceId: NamespaceId?,
    val name: FuncName?,
    val inputs: FuncInputs?,
    val code: CodeType?
)

@Serializable
data class InvokeFuncReq(
    val correlationId: CorrelationId?,
    val inputs: InvocationInputs?,
)
