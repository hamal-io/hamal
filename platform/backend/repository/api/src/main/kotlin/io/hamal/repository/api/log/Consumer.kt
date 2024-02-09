package io.hamal.repository.api.log

import io.hamal.lib.common.domain.BatchSize
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.LogTopicId
import kotlin.reflect.KClass

class LogConsumerId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value))
}

interface LogConsumer<VALUE : Any> {
    val consumerId: LogConsumerId

    fun consume(limit: Limit, fn: (LogEventId, VALUE) -> Unit): Int {
        return consumeIndexed(limit) { _, logEventId, value -> fn(logEventId, value) }
    }

    fun consumeIndexed(limit: Limit, fn: (Int, LogEventId, VALUE) -> Unit): Int
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

    override fun consumeIndexed(limit: Limit, fn: (Int, LogEventId, Value) -> Unit): Int {
        val eventsToConsume = repository.consume(consumerId, topicId, limit)

        eventsToConsume.mapIndexed { index, event ->
            fn(
                index,
                event.id,
                json.decompressAndDeserialize(valueClass, event.bytes)
            )
            event.id
        }.maxByOrNull { it }?.let { repository.commit(consumerId, topicId, it) }
        return eventsToConsume.size
    }
}


class LogConsumerBatchImpl<Value : Any>(
    override val consumerId: LogConsumerId,
    private val topicId: LogTopicId,
    private val repository: LogBrokerRepository,
    private val valueClass: KClass<Value>
) : LogConsumerBatch<Value> {

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