package io.hamal.lib.domain.req

import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.InvocationSecrets
import kotlinx.serialization.Serializable

@Serializable
data class AdhocInvocationReq(
    val inputs: InvocationInputs,
    val secrets: InvocationSecrets,
    val code: Code
)