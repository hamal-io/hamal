package io.hamal.repository.api.log

import kotlin.reflect.KClass

interface LogConsumer<VALUE : Any> {
    val consumerId: ConsumerId
    fun consume(limit: Int, fn: (ChunkId, VALUE) -> Unit): Int {
        return consumeIndexed(limit) { _, chunkId, value -> fn(chunkId, value) }
    }

    fun consumeIndexed(limit: Int, fn: (Int, ChunkId, VALUE) -> Unit): Int
}

@JvmInline
value class ConsumerId(val value: String) //FIXME become VO

interface BatchConsumer<VALUE : Any> {
    val consumerId: ConsumerId

    // min batch size
    // max batch size
    fun consumeBatch(batchSize: Int, block: (List<VALUE>) -> Unit): Int

}

class LogConsumerImpl<Value : Any>(
    override val consumerId: ConsumerId,
    private val topic: Topic,
    private val repository: BrokerRepository,
    private val valueClass: KClass<Value>
) : LogConsumer<Value> {

    override fun consumeIndexed(limit: Int, fn: (Int, ChunkId, Value) -> Unit): Int {
        val chunksToConsume = repository.consume(consumerId, topic, limit)

        chunksToConsume.mapIndexed { index, chunk ->
            fn(
                index,
                chunk.id,
                json.decompressAndDeserialize(valueClass, chunk.bytes)
            )
            chunk.id
        }.maxByOrNull { it }?.let { repository.commit(consumerId, topic, it) }
        return chunksToConsume.size
    }
}

class BatchConsumerImpl<Value : Any>(
    override val consumerId: ConsumerId,
    private val topic: Topic,
    private val repository: BrokerRepository,
    private val valueClass: KClass<Value>
) : BatchConsumer<Value> {

    override fun consumeBatch(batchSize: Int, block: (List<Value>) -> Unit): Int {
        val chunksToConsume = repository.consume(consumerId, topic, batchSize)

        if (chunksToConsume.isEmpty()) {
            return 0
        }

        val batch = chunksToConsume.map { chunk ->
            json.decompressAndDeserialize(valueClass, chunk.bytes)
        }

        block(batch)
        chunksToConsume.maxByOrNull { chunk -> chunk.id }?.let { repository.commit(consumerId, topic, it.id) }
        return batch.size
    }
}