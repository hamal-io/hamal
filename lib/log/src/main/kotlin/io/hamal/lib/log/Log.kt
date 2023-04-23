package io.hamal.lib.log

import java.nio.ByteBuffer
import java.time.Instant

data class ToRecord(
    val key: ByteBuffer,
    val value: ByteBuffer,
    val instant: Instant
)