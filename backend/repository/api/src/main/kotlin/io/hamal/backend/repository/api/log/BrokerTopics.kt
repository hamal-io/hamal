package io.hamal.backend.repository.api.log

import java.io.Closeable

interface BrokerTopicsRepository : Closeable {
    fun resolveTopic(name: Topic.Name): Topic
}
