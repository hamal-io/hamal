package io.hamal.lib.log.broker

import io.hamal.lib.log.topic.Topic
import io.hamal.lib.meta.KeyedOnce
import java.nio.file.Path

data class Broker(
    val id: Id,
    val path: Path
) {
    @JvmInline
    value class Id(val value: ULong) {
        constructor(value: Int) : this(value.toULong())
    }
}

interface AppendToTopic {
    fun append(topicId: Topic.Id, vararg bytes: ByteArray)
}

interface ReadFromTopic {

}

interface ResolveTopic {
    fun resolveTopic(topicName: String): Topic
}

class BrokerRepository private constructor(

) : AppendToTopic, ReadFromTopic, AutoCloseable, ResolveTopic {

    companion object {
        fun open(broker: Broker): BrokerRepository {
            TODO()
        }
    }

    override fun resolveTopic(topicName: String): Topic {
        TODO("Not yet implemented")
    }

    override fun append(topicId: Topic.Id, vararg bytes: ByteArray) {
//        val topic = getTopic(topicId)
//        topic.append(toRecord)
        TODO()
    }

    fun getTopic(topicId: Topic.Id): Topic = topicMapping.invoke(topicId) {
//        Topic.open(
//            Topic.Config(
//                topicId,
//                Path("/tmp/hamal/topics")
//            )
//        )
        TODO()
    }

    private val topicMapping = KeyedOnce.default<Topic.Id, Topic>()

    override fun close() {
        TODO("Not yet implemented")
    }
}