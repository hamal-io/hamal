package io.hamal.backend.repository.api.domain

import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.StatePayload
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.InvocationSecrets
import kotlinx.serialization.Serializable


@Serializable
data class Req(
    val id: ReqId,
    // parentReqId?
    val payload: ReqPayload
    // requested when
    // requested by
)

@Serializable
sealed interface ReqPayload {
    @Serializable
    data class InvokeAdhoc(
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

