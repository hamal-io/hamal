package io.hamal.repository.api.log

import io.hamal.lib.common.domain.BatchSize
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.ValueSnowflakeId
import io.hamal.lib.common.value.ValueVariableSnowflakeId
import io.hamal.lib.domain.vo.LogTopicId
import io.hamal.lib.domain.vo.TopicEventId
import io.hamal.lib.domain.vo.TopicEventPayload
import io.hamal.repository.api.TopicEvent
import kotlin.reflect.KClass

class LogConsumerId(override val value: ValueSnowflakeId) : ValueVariableSnowflakeId() {
    companion object {
        fun LogConsumerId(value: SnowflakeId) = LogConsumerId(ValueSnowflakeId(value))
        fun LogConsumerId(value: Int) = LogConsumerId(ValueSnowflakeId(SnowflakeId(value.toLong())))
        fun LogConsumerId(value: String) = LogConsumerId(ValueSnowflakeId(SnowflakeId(value.toLong(16))))
    }
}

interface LogConsumer<VALUE : Any> {
    val consumerId: LogConsumerId

    fun consume(limit: Limit, fn: (Int, LogEventId, VALUE) -> Unit): Int
}

interface LogConsumerBatch<VALUE : Any> {
    val consumerId: LogConsumerId

    // min batch size
    // max batch size
    fun consumeBatch(batchSize: BatchSize, block: (List<VALUE>) -> Unit): Int
}

class LogConsumerImpl<Value : Any>(
    override val consumerId: LogConsumerId,
    private val topicId: LogTopicId,
    private val repository: LogBrokerRepository,
    private val valueClass: KClass<Value>
) : LogConsumer<Value> {

    @Synchronized
    override fun consume(limit: Limit, fn: (Int, LogEventId, Value) -> Unit): Int {
        val eventsToConsume = repository.consume(consumerId, topicId, limit)

        eventsToConsume.mapIndexed { index, event ->
            fn(
                index,
                event.id,
                json.decompressAndDeserialize(valueClass, event.bytes)
            )
            repository.commit(consumerId, topicId, event.id)
        }
        return eventsToConsume.size
    }
}


class LogConsumerBatchImpl<Value : Any>(
    override val consumerId: LogConsumerId,
    private val topicId: LogTopicId,
    private val repository: LogBrokerRepository,
    private val valueClass: KClass<Value>
) : LogConsumerBatch<Value> {

    @Synchronized
    override fun consumeBatch(batchSize: BatchSize, block: (List<Value>) -> Unit): Int {
        val eventsToConsume = repository.consume(consumerId, topicId, Limit(batchSize.value))

        if (eventsToConsume.isEmpty()) {
            return 0
        }

        val batch = eventsToConsume.map { chunk ->
            json.decompressAndDeserialize(valueClass, chunk.bytes)
        }

        block(batch)
        eventsToConsume.maxByOrNull { chunk -> chunk.id }?.let { repository.commit(consumerId, topicId, it.id) }
        return batch.size
    }
}


class TopicEventConsumerBatchImpl(
    override val consumerId: LogConsumerId,
    private val topicId: LogTopicId,
    private val repository: LogBrokerRepository,
) : LogConsumerBatch<TopicEvent> {

    @Synchronized
    override fun consumeBatch(batchSize: BatchSize, block: (List<TopicEvent>) -> Unit): Int {
        val eventsToConsume = repository.consume(consumerId, topicId, Limit(batchSize.value))

        if (eventsToConsume.isEmpty()) {
            return 0
        }

        val batch = eventsToConsume.map { evt ->
            TopicEvent(
                id = TopicEventId(evt.id.value),
                payload = json.decompressAndDeserialize(TopicEventPayload::class, evt.bytes)
            )
        }

        block(batch)
        eventsToConsume.maxByOrNull { chunk -> chunk.id }?.let { repository.commit(consumerId, topicId, it.id) }
        return batch.size
    }
}