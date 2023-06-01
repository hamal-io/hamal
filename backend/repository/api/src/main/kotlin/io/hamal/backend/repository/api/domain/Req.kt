package io.hamal.backend.repository.api.domain

import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.StatePayload
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.InvocationSecrets
import kotlinx.serialization.Serializable

@Serializable
data class Req(
    val id: ReqId,
    val status: ReqStatus,
    val payload: ReqPayload
)

@Serializable
sealed interface ReqPayload {
    @Serializable
    data class InvokeAdhoc(
        val execId: ExecId,
        val inputs: InvocationInputs,
        val secrets: InvocationSecrets,
        val code: Code
    ) : ReqPayload

    @Serializable
    data class CompleteExec(
        val execId: ExecId,
        val statePayload: StatePayload
    ) : ReqPayload

}

