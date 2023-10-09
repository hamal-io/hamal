package io.hamal.core.service

import io.hamal.core.component.Async
import io.hamal.core.event.PlatformEventContainer
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.util.HashUtils.md5
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.repository.api.event.PlatformEvent
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.ConsumerId
import io.hamal.repository.api.log.CreateTopic.TopicToCreate
import io.hamal.repository.api.log.ProtobufLogConsumer
import org.springframework.beans.factory.DisposableBean
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Service
import java.util.concurrent.ScheduledFuture
import kotlin.time.Duration.Companion.milliseconds

@Service
internal class PlatformEventService(
    private val async: Async,
    private val generateDomainId: GenerateDomainId,
    private val platformEventContainer: PlatformEventContainer,
    private val platformEventBrokerRepository: BrokerRepository
) : DisposableBean, ApplicationListener<ContextRefreshedEvent> {

    private val scheduledTasks = mutableListOf<ScheduledFuture<*>>()

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        val instanceTopics = platformEventContainer.topicNames().map { topicName ->
            val topicId = generateDomainId(::TopicId)
            platformEventBrokerRepository.findTopic(GroupId.root, topicName) ?: platformEventBrokerRepository.create(
                CmdId(topicId), TopicToCreate(topicId, topicName, GroupId.root)
            )
        }

        instanceTopics.forEach { topic ->
            val consumer = ProtobufLogConsumer(
                ConsumerId("core-event-service"), topic, platformEventBrokerRepository, PlatformEvent::class
            )

            scheduledTasks.add(
                async.atFixedRate(1.milliseconds) {
                    consumer.consume(10) { chunkId, evt ->
                        platformEventContainer[evt::class].forEach { handler ->
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