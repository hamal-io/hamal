package io.hamal.module.bus.api.common

class Record<KEY : Any, VALUE : Any>(
    val key: KEY,
    val value: VALUE,
    val topicId: TopicId
) {

}