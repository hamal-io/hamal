package io.hamal.module.bus.impl.consumer

import io.hamal.module.bus.api.common.Message
import io.hamal.module.bus.api.common.TopicId
import io.hamal.module.bus.api.consumer.Consumer
import io.hamal.module.bus.impl.core.InMemoryBroker

class InMemoryConsumer<KEY : Any, VALUE : Any>(
    val topicIds: List<TopicId>
) : Consumer<KEY, VALUE> {
    override fun invoke(): List<Message<KEY, VALUE>> {
        return topicIds.mapNotNull { topicId ->
            InMemoryBroker.queueStore[topicId]?.take()
        }.map { it as Message<KEY, VALUE> }
            .toList()
    }
}