package io.hamal.backend.repository.api.log

interface Appender<VALUE : Any> {
    fun append(topic: Topic, value: VALUE)
}