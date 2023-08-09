package io.hamal.lib.domain.req

import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.kua.value.CodeValue
import kotlinx.serialization.Serializable

@Serializable
data class CreateFuncReq(
    val name: FuncName,
    val inputs: FuncInputs,
    val code: CodeValue
)


@Serializable
data class UpdateFuncReq(
    val name: FuncName,
    val inputs: FuncInputs,
    val code: CodeValue
)

@Serializable
data class InvokeFuncReq(
    val correlationId: CorrelationId?,
    val inputs: InvocationInputs?,
)
