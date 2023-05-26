package io.hamal.backend.service

import io.hamal.backend.event.TriggerInvocationEvent
import io.hamal.backend.event.component.EventEmitter
import io.hamal.backend.repository.api.TriggerQueryRepository
import io.hamal.backend.service.query.FuncQueryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class ScheduleTriggerService
@Autowired constructor(
    private val funcQueryService: FuncQueryService,
    private val triggerQueryRepository: TriggerQueryRepository,
    private val eventEmitter: EventEmitter
) {

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.SECONDS)
    fun run() {
        val triggers = triggerQueryRepository.query { }

        triggers.forEach { trigger ->
            println("invoking trigger ${trigger.id}")
            eventEmitter.emit(
                TriggerInvocationEvent(
                    shard = trigger.shard,
                    func = funcQueryService.get(trigger.funcId),
                    trigger = trigger
                )
            )
        }
    }
}