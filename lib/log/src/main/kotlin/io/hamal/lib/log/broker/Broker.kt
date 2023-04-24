package io.hamal.lib.log.broker

import io.hamal.lib.log.ToRecord
import io.hamal.lib.log.topic.Topic
import io.hamal.lib.meta.KeyedOnce
import kotlin.io.path.Path

interface Broker {
    fun append(topicId: Topic.Id, toRecord: ToRecord)

    //FIXME remove from BROKER just for prototyping
    fun getTopic(topicId: Topic.Id): Topic

}

class DefaultBroker : Broker {


    override fun append(topicId: Topic.Id, toRecord: ToRecord) {
        val topic = getTopic(topicId)
        topic.append(toRecord)
    }

    override fun getTopic(topicId: Topic.Id): Topic = topicMapping.invoke(topicId) {
        Topic.open(
            Topic.Config(
                topicId,
                Path("/tmp/hamal/topics")
            )
        )
    }

    private val topicMapping = KeyedOnce.default<Topic.Id, Topic>()
}