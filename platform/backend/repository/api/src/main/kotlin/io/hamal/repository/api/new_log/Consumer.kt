package io.hamal.repository.api.new_log

import io.hamal.lib.common.domain.BatchSize
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.snowflake.SnowflakeId
import kotlin.reflect.KClass

class LogConsumerId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value))
}

interface LogConsumer<VALUE : Any> {
    val consumerId: LogConsumerId

    fun consume(limit: Limit, fn: (LogEntryId, VALUE) -> Unit): Int {
        return consumeIndexed(limit) { _, LogEntryId, value -> fn(LogEntryId, value) }
    }

    fun consumeIndexed(limit: Limit, fn: (Int, LogEntryId, VALUE) -> Unit): Int
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

    override fun consumeIndexed(limit: Limit, fn: (Int, LogEntryId, Value) -> Unit): Int {
        val entriesToConsume = repository.consume(consumerId, topicId, limit)

        entriesToConsume.mapIndexed { index, chunk ->
            fn(
                index,
                chunk.id,
                io.hamal.repository.api.log.json.decompressAndDeserialize(valueClass, chunk.bytes)
            )
            chunk.id
        }.maxByOrNull { it }?.let { repository.commit(consumerId, topicId, it) }
        return entriesToConsume.size
    }
}


class LogConsumerBatchImpl<Value : Any>(
    override val consumerId: LogConsumerId,
    private val topicId: LogTopicId,
    private val repository: LogBrokerRepository,
    private val valueClass: KClass<Value>
) : LogConsumerBatch<Value> {

    override fun consumeBatch(batchSize: BatchSize, block: (List<Value>) -> Unit): Int {
        val entriesToConsume = repository.consume(consumerId, topicId, Limit(batchSize.value))

        if (entriesToConsume.isEmpty()) {
            return 0
        }

        val batch = entriesToConsume.map { chunk ->
            io.hamal.repository.api.log.json.decompressAndDeserialize(valueClass, chunk.bytes)
        }

        block(batch)
        entriesToConsume.maxByOrNull { chunk -> chunk.id }?.let { repository.commit(consumerId, topicId, it.id) }
        return batch.size
    }
}