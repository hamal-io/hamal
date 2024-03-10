package io.hamal.core.service

import io.hamal.core.component.Scheduler
import io.hamal.core.component.WorkerPool
import io.hamal.core.event.InternalEventContainer
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.logger
import io.hamal.lib.domain.GenerateCmdId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.repository.api.TopicRepository
import io.hamal.repository.api.event.InternalEvent
import io.hamal.repository.api.log.LogBrokerRepository
import io.hamal.repository.api.log.LogConsumerId
import io.hamal.repository.api.log.LogConsumerImpl
import org.springframework.beans.factory.DisposableBean
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Service
import java.util.concurrent.ScheduledFuture
import kotlin.time.Duration.Companion.milliseconds

private val log = logger(InternalEventService::class)

@Service
class InternalEventService(
    private val scheduler: Scheduler,
    private val workerPool: WorkerPool,
    private val internalEventContainer: InternalEventContainer,
    private val topicRepository: TopicRepository,
    private val logBrokerRepository: LogBrokerRepository,
    private val generateCmdId: GenerateCmdId
) : DisposableBean, ApplicationListener<ApplicationReadyEvent> {


    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        internalEventContainer.topicNames().forEach { topicName ->
            val topic = topicRepository.getTopic(NamespaceId.root, topicName)
            val consumer = LogConsumerImpl(
                consumerId = LogConsumerId(topic.id.value),
                topicId = topic.logTopicId,
                repository = logBrokerRepository,
                valueClass = InternalEvent::class
            )

            scheduler.atFixedRate(1.milliseconds) {
                consumer.consume(Limit(10)) { _, _, evt ->
                    workerPool.execute {
                        internalEventContainer[evt::class].forEach { handler ->
                            val cmdId = generateCmdId()
                            handler.handle(cmdId, evt)
                        }
                    }
                }
            }
        }
    }

    override fun destroy() {
        scheduledTasks.forEach {
            it.cancel(true)
        }
    }

    private val scheduledTasks = mutableListOf<ScheduledFuture<*>>()
}
