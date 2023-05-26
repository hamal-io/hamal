package io.hamal.backend.service

import io.hamal.backend.repository.api.TriggerQueryRepository
import io.hamal.backend.service.cmd.AdhocCmdService
import io.hamal.backend.system_agent.SystemAgent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

@Service
class ScheduleTriggerService
@Autowired constructor(
    private val triggerQueryRepository: TriggerQueryRepository,
    private val adhoc: AdhocCmdService
) {


    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.SECONDS)
    fun run() {
        val triggers = triggerQueryRepository.query { }

        val time = measureTimeMillis {
            triggers.forEach { trigger ->
//                agent.run(trigger.code)
                SystemAgent.run(trigger.code)
//                adhoc.execute(
//                    AdhocCmdService.AdhocToExecute(
//                        reqId = ReqId(1),
//                        shard = trigger.shard,
//                        code = trigger.code
//                    )
//                )
            }
        }

        println("Took: $time ms")
        repeat(3) { println() }
    }
}