package io.hamal.backend.repository.api.domain

import io.hamal.lib.domain.ComputeId
import io.hamal.lib.domain.StatePayload
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.InvocationSecrets
import kotlinx.serialization.Serializable


@Serializable
sealed interface Req {
    val id: ComputeId
    val status: ReqStatus

    // parentComputeId?
    val payload: ReqPayload
    // requested when
    // requested by
}

@Serializable
data class ReceivedReq(
    override val id: ComputeId,
    // parentComputeId?
    override val payload: ReqPayload
) : Req {
    override val status = ReqStatus.Received
}


@Serializable
data class InFlightReq(
    override val id: ComputeId,
    // parentComputeId?
    override val payload: ReqPayload
) : Req {
    override val status = ReqStatus.InFlight
}


@Serializable
data class CompletedReq(
    override val id: ComputeId,
    // parentComputeId?
    override val payload: ReqPayload
) : Req {
    override val status = ReqStatus.Completed
}

@Serializable
data class FailedReq(
    override val id: ComputeId,
    // parentComputeId?
    override val payload: ReqPayload
) : Req {
    override val status = ReqStatus.Failed
}


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

