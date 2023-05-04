package io.hamal.backend.repository.api.log

interface BrokerTopicsRepository : AutoCloseable {
    fun resolveTopic(name: Topic.Name): Topic
    fun count(): ULong
}
