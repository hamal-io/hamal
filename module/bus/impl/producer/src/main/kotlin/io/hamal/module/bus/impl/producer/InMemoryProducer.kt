package io.hamal.module.bus.impl.producer

import io.hamal.module.bus.api.common.Message
import io.hamal.module.bus.api.producer.Producer
import io.hamal.module.bus.impl.core.InMemoryBroker.queueStore
import java.util.concurrent.LinkedBlockingQueue

class InMemoryProducer<KEY : Any, VALUE : Any> : Producer<KEY, VALUE> {
    override fun invoke(messages: List<Message<KEY, VALUE>>) {
        messages.forEach { message ->
            queueStore.putIfAbsent(message.meta.topicId, LinkedBlockingQueue())
            queueStore[message.meta.topicId]!!.add(message)
        }
    }
}