package io.hamal.backend.repository.api.domain

import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.StatePayload
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
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
    data class InvokeOneshot(
        val execId: ExecId,
        val correlationId: CorrelationId,
        val inputs: InvocationInputs,
        val secrets: InvocationSecrets,
        val func: Func
    ) : ReqPayload

    @Serializable
    data class InvokeFixedRate(
        val execId: ExecId,
        val correlationId: CorrelationId,
        val inputs: InvocationInputs,
        val secrets: InvocationSecrets,
        val func: Func,
        val trigger: FixedRateTrigger
    ) : ReqPayload

    @Serializable
    data class InvokeEvent(
        val execId: ExecId,
        val correlationId: CorrelationId,
        val inputs: InvocationInputs,
        val secrets: InvocationSecrets,
        val func: Func,
        val trigger: EventTrigger,
    ) : ReqPayload


    @Serializable
    data class CompleteExec(
        val execId: ExecId,
        val statePayload: StatePayload
    ) : ReqPayload

}

