package io.hamal.backend.instance.req.handler

import io.hamal.backend.instance.BaseIT
import io.hamal.backend.repository.api.log.CreateTopic
import io.hamal.backend.repository.memory.log.MemoryLogTopic
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import java.util.concurrent.atomic.AtomicInteger

internal object NextCommandId {
    operator fun invoke(): CmdId {
        return CmdId(counter.incrementAndGet())
    }

    private val counter = AtomicInteger(0)
}

internal abstract class BaseReqHandlerIT : BaseIT() {
    fun createTopic(name: TopicName): MemoryLogTopic {
        return eventBrokerRepository.create(
            NextCommandId(), CreateTopic.TopicToCreate(
                id = generateDomainId(::TopicId),
                name = name
            )
        )
    }
}