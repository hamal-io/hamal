package io.hamal.lib.sdk.domain

import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.FuncRef
import kotlinx.serialization.Serializable

@Serializable
data class ApiAgentRequest(
    val id: ExecId,
    val reference: FuncRef,
    val code: Code
)

@Serializable
data class ApiAgentRequests(
    val requests: List<ApiAgentRequest>
)