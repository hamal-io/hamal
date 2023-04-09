package io.hamal.module.bus.impl.core

import io.hamal.module.bus.api.common.Record
import io.hamal.module.bus.api.common.TopicId
import java.util.concurrent.LinkedBlockingQueue

object InMemoryBroker {

    val queueStore = mutableMapOf<TopicId, LinkedBlockingQueue<Record<*, *>>>()

}