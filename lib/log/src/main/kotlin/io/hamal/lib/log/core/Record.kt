package io.hamal.lib.log.core

import java.nio.ByteBuffer

@JvmInline
value class RecordOffset(val value: ULong) {
    constructor(value: Int) : this(value.toULong())
}

data class Record(
    val key: ByteBuffer,
    val value: ByteBuffer
)