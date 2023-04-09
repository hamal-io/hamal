package io.hamal.module.bus.impl.producer

import io.hamal.module.bus.api.common.Record
import io.hamal.module.bus.api.producer.Producer
import io.hamal.module.bus.impl.core.InMemoryBroker.queueStore
import java.util.concurrent.LinkedBlockingQueue

class InMemoryProducer<KEY : Any, VALUE : Any> : Producer<KEY, VALUE> {
    override fun invoke(records: List<Record<KEY, VALUE>>) {
        records.forEach { message ->
            queueStore.putIfAbsent(message.topicId, LinkedBlockingQueue())
            queueStore[message.topicId]!!.add(message)
        }
    }
}