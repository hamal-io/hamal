package io.hamal.lib.domain.req

import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.kua.type.CodeType
import kotlinx.serialization.Serializable

@Serializable
data class InvokeAdhocReq(
    val inputs: InvocationInputs,
    val code: CodeType
)