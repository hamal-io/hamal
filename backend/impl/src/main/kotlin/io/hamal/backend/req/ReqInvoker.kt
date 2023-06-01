package io.hamal.backend.req

import io.hamal.backend.repository.api.ReqCmdRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

//FIXME become service ReqHandler with loop
@Service
class ReqInvoker private constructor(
    private val reqCmdRepository: ReqCmdRepository,
    private val regRegistry: ReqRegistry,
) {

//    fun <REQ : Request> invoke(req: REQ) {
//        val handler = regRegistry[req::class]
//        handler(req)
//    }


    @Scheduled(fixedRate = 10, initialDelay = 100, timeUnit = TimeUnit.MILLISECONDS)
    fun run() {
        println("ReqInvoker")
        reqCmdRepository.dequeue(1)
            .forEach { req ->
                try {

                    regRegistry[req::class](req)

//                    val computeId = req.id.toComputeId()
//                    val shard = Shard(1)
//
//                    when (val payload = req.payload) {
//                        is ReqPayload.InvokeAdhoc -> handle(computeId, shard, payload)
//                        is ReqPayload.InvokeOneshot -> handle(computeId, shard, payload)
//                        is ReqPayload.InvokeFixedRate -> handle(computeId, shard, payload)
//                        is ReqPayload.InvokeEvent -> handle(computeId, shard, payload)
//                        is ReqPayload.CompleteExec -> handle(computeId, shard, payload)
//                        else -> TODO()
//                    }

                    reqCmdRepository.complete(req.id)
                } catch (t: Throwable) {
                    reqCmdRepository.fail(req.id)
                }
            }

    }


}
