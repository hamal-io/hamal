package io.hamal.core.service

import io.hamal.core.component.Async
import io.hamal.core.event.InternalEventContainer
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.util.HashUtils.md5
import io.hamal.lib.domain.vo.GroupId
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

@Service
class InternalEventService(
    private val async: Async,
    private val internalEventContainer: InternalEventContainer,
    private val topicRepository: TopicRepository,
    private val logBrokerRepository: LogBrokerRepository
) : DisposableBean, ApplicationListener<ApplicationReadyEvent> {

    private val scheduledTasks = mutableListOf<ScheduledFuture<*>>()

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        reload()
    }

    override fun destroy() {
        scheduledTasks.forEach {
            it.cancel(true)
        }
    }

    fun reload() {
        destroy()
        scheduledTasks.clear()

        internalEventContainer.topicNames().forEach { topicName ->
            val topic = topicRepository.getGroupTopic(GroupId.root, topicName)
            val consumer = LogConsumerImpl(
                consumerId = LogConsumerId(SnowflakeId(1)),
                topicId = topic.logTopicId,
                repository = logBrokerRepository,
                valueClass = InternalEvent::class
            )
            scheduledTasks.add(
                async.atFixedRate(1.milliseconds) {
                    consumer.consume(Limit(10)) { chunkId, evt ->
                        internalEventContainer[evt::class].forEach { handler ->
                            val cmdId = CmdId(md5("${evt.topicName}-${chunkId.value}"))
                            handler.handle(cmdId, evt)
                        }
                    }
                },
            )
        }
    }
}