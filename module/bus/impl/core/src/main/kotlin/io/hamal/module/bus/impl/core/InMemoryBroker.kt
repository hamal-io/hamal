package io.hamal.module.bus.impl.core

import io.hamal.module.bus.api.common.Message
import io.hamal.module.bus.api.common.TopicId
import java.util.concurrent.LinkedBlockingQueue

object InMemoryBroker {

    val queueStore = mutableMapOf<TopicId, LinkedBlockingQueue<Message<*, *>>>()

}