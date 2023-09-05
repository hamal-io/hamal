package io.hamal.api.service

import io.hamal.api.component.Async
import io.hamal.api.event.HubEventContainer
import io.hamal.repository.api.event.HubEvent
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.util.HashUtils.md5
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.repository.api.log.CreateTopic
import io.hamal.repository.api.log.GroupId
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.ProtobufLogConsumer
import org.springframework.beans.factory.DisposableBean
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Service
import java.util.concurrent.ScheduledFuture
import kotlin.time.Duration.Companion.milliseconds

@Service
class HubEventService(
    private val async: Async,
    private val generateDomainId: GenerateDomainId,
    private val hubEventContainer: HubEventContainer,
    private val hubEventBrokerRepository: BrokerRepository
) : DisposableBean, ApplicationListener<ContextRefreshedEvent> {

    private val scheduledTasks = mutableListOf<ScheduledFuture<*>>()

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        val instanceTopics = hubEventContainer.topicNames().map { topicName ->
            val topicId = generateDomainId(::TopicId)
            hubEventBrokerRepository.findTopic(topicName) ?: hubEventBrokerRepository.create(
                CmdId(topicId), CreateTopic.TopicToCreate(topicId, topicName)
            )
        }

        instanceTopics.forEach { topic ->
            val consumer = ProtobufLogConsumer(
                GroupId("instance-event-service"), topic, hubEventBrokerRepository, HubEvent::class
            )

            scheduledTasks.add(
                async.atFixedRate(1.milliseconds) {
                    consumer.consume(10) { chunkId, evt ->
                        hubEventContainer[evt::class].forEach { handler ->
                            val cmdId = CmdId(md5("${evt.topicName}-${chunkId.value.value}"))
                            handler.handle(cmdId, evt)
                        }
                    }
                },
            )
        }
    }


    override fun destroy() {
        scheduledTasks.forEach {
            it.cancel(false)
        }
    }

}