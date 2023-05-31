package io.hamal.backend.event

import io.hamal.backend.repository.api.domain.Req
import io.hamal.lib.common.Shard
import kotlinx.serialization.Serializable

@Serializable
@EventTopic("requested")
data class RequestedEvent(
    //computeId
//    override val computeId: ComputeId,
    override val shard: Shard,
    val req: Req
) : Event()

//@Serializable
//@SystemEventTopic("req::invoke_adhoc")
//data class InvokeAdhocEvt(
//    override val shard: Shard,
//    val inputs: InvocationInputs,
//    val secrets: InvocationSecrets,
//    val code: Code
//) : Event()
//
//@Serializable
//@SystemEventTopic("req::complete_exec")
//data class CompleteExecEvt(
//    override val shard: Shard,
//    val computeId: ComputeId,
//    val execId: ExecId,
//    val statePayload: StatePayload
//) : Event()

// create func
// create trigger
// invoke func