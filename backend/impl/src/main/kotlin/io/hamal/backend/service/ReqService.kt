//package io.hamal.backend.service
//
//import io.hamal.backend.repository.api.ReqCmdRepository
//import io.hamal.backend.repository.api.domain.AdhocInvocation
//import io.hamal.backend.repository.api.domain.EventInvocation
//import io.hamal.backend.repository.api.domain.InFlightExec
//import io.hamal.backend.repository.api.domain.OneshotInvocation
//import io.hamal.backend.repository.api.domain.ReqPayload.*
//import io.hamal.backend.service.cmd.ExecCmdService
//import io.hamal.backend.service.cmd.ExecCmdService.ToPlan
//import io.hamal.backend.service.cmd.StateCmdService
//import io.hamal.backend.service.query.ExecQueryService
//import io.hamal.lib.common.Shard
//import io.hamal.lib.domain.ComputeId
//import io.hamal.lib.domain.Correlation
//import io.hamal.lib.domain.ReqId
//import io.hamal.lib.domain.vo.ExecInputs
//import io.hamal.lib.domain.vo.ExecSecrets
//import io.hamal.lib.domain.vo.InvocationInputs
//import io.hamal.lib.domain.vo.InvocationSecrets
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.scheduling.annotation.Scheduled
//import org.springframework.stereotype.Service
//import java.util.concurrent.TimeUnit
//
//@Service
//class ReqService
//@Autowired constructor(
//    val reqCmdRepository: ReqCmdRepository,
//    val execCmdService: ExecCmdService,
//    val execQueryService: ExecQueryService,
//    val stateCmdService: StateCmdService
//) {
//    @Scheduled(fixedRate = 10, initialDelay = 100, timeUnit = TimeUnit.MILLISECONDS)
//    fun processRequests() {
//        reqCmdRepository.dequeue(1)
//            .forEach { req ->
//                try {
//                    val computeId = req.id.toComputeId()
//                    val shard = Shard(1)
//
//                    when (val payload = req.payload) {
//                        is InvokeAdhoc -> handle(computeId, shard, payload)
//                        is InvokeOneshot -> handle(computeId, shard, payload)
//                        is InvokeFixedRate -> handle(computeId, shard, payload)
//                        is InvokeEvent -> handle(computeId, shard, payload)
//                        is CompleteExec -> handle(computeId, shard, payload)
//                        else -> TODO()
//                    }
//
//                    reqCmdRepository.complete(req.id)
//                } catch (t: Throwable) {
//                    reqCmdRepository.fail(req.id)
//                }
//            }
//
//    }
//}
//
//internal fun ReqId.toComputeId() = ComputeId(value)
//
//internal fun ReqService.handle(computeId: ComputeId, shard: Shard, toInvoke: InvokeAdhoc) {
//    execCmdService.plan(
//        computeId, ToPlan(
//            execId = toInvoke.execId,
//            shard = shard,
//            correlation = null,
//            inputs = toInvoke.inputs.toExecInputs(),
//            secrets = toInvoke.secrets.toExecSecrets(),
//            code = toInvoke.code,
//            // FIXME func for audit purpose ?
//            invocation = AdhocInvocation()
//        )
//    )
//}
//
//internal fun ReqService.handle(computeId: ComputeId, shard: Shard, toInvoke: InvokeOneshot) {
//    val func = toInvoke.func
//
//    execCmdService.plan(
//        computeId, ToPlan(
//            execId = toInvoke.execId,
//            shard = shard,
//            code = func.code,
//            correlation = Correlation(
//                correlationId = toInvoke.correlationId,
//                funcId = func.id
//            ),
//            inputs = toInvoke.inputs.toExecInputs(),
//            secrets = toInvoke.secrets.toExecSecrets(),
//            // FIXME func for audit purpose ?
//            invocation = OneshotInvocation()
//        )
//    )
//
//}
//
//internal fun ReqService.handle(computeId: ComputeId, shard: Shard, toInvoke: InvokeFixedRate) {
//    val func = toInvoke.func
//
//    execCmdService.plan(
//        computeId, ToPlan(
//            execId = toInvoke.execId,
//            shard = shard,
//            code = func.code,
//            correlation = Correlation(
//                correlationId = toInvoke.correlationId,
//                funcId = func.id
//            ),
//            inputs = toInvoke.inputs.toExecInputs(),
//            secrets = toInvoke.secrets.toExecSecrets(),
//            // FIXME func for audit purpose ?
//            invocation = OneshotInvocation()
//        )
//    )
//
//}
//
//internal fun ReqService.handle(computeId: ComputeId, shard: Shard, toInvoke: InvokeEvent) {
//    val func = toInvoke.func
//
//    execCmdService.plan(
//        computeId, ToPlan(
//            execId = toInvoke.execId,
//            shard = shard,
//            code = func.code,
//            correlation = Correlation(
//                correlationId = toInvoke.correlationId,
//                funcId = func.id
//            ),
//            inputs = toInvoke.inputs.toExecInputs(),
//            secrets = toInvoke.secrets.toExecSecrets(),
//            //FIXME events maybe as input
//            // FIXME func for audit purpose ?
//            invocation = EventInvocation()
//        )
//    )
//
//}
//
//
//internal fun ReqService.handle(computeId: ComputeId, shard: Shard, toComplete: CompleteExec) {
//    execQueryService.find(toComplete.execId)
//        ?.let { exec ->
//            if (exec is InFlightExec) {
//
//                if (exec.correlation != null) {
//                    stateCmdService.set(
//                        computeId, StateCmdService.StateToSet(
//                            shard = shard,
//                            correlation = exec.correlation!!,
//                            payload = toComplete.statePayload
//                        )
//                    )
//                }
//
//                execCmdService.complete(
//                    computeId = computeId,
//                    inFlightExec = exec
//                )
//            }
//        }
//}
//
//private fun InvocationInputs.toExecInputs() = ExecInputs(this.value)
//private fun InvocationSecrets.toExecSecrets() = ExecSecrets(this.value)