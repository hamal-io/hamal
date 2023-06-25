package io.hamal.lib.sdk.domain

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable


@Deprecated("do not have separate dto")
@Serializable
data class ApiAgentExecRequests(
    val requests: List<ExecRequest>
) {

    @Serializable
    data class ExecRequest(
        val id: ExecId,
        val funcName: FuncName,
        val correlation: Correlation?,
        val inputs: ExecInputs,
        val secrets: ExecSecrets,
        val statePayload: State?,
        val code: Code
    )
}
