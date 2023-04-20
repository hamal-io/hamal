package io.hamal.module.bus.api.common

import java.time.Instant

@JvmInline
value class MessageSize(val value: Long)

@JvmInline
value class MessageOffset(val value: Long)

@JvmInline
value class KeySize(val value: UByte)

@JvmInline
value class ValueSize(val value: UShort)


data class Message<KEY : Any, VALUE : Any>(
    val meta: Meta,
    val key: KEY,
    val value: VALUE,
) {
    data class Meta(
        val version: Byte,
        val instant: Instant,
        val topicId: TopicId,
        val partition: Partition,
        val offset: MessageOffset,
        val crc32: Int
    )
}
