package io.hamal.lib.util

import java.time.Instant


interface Snowflake {

    interface TimeSource {
        fun elapsed(): Long
    }

    interface Generator {
        fun next(): Id
    }

    @JvmInline
    value class Id(val value: Long)
}

internal object DefaultTimeSource : Snowflake.TimeSource {
    val epoch = Instant.ofEpochMilli(1682116276624) // 2023-04-22 some when in the morning AEST
    val startedAt = millis()
    override fun elapsed() = millis() - startedAt
    private fun millis() = (System.nanoTime() / 1_000_000) - epoch.toEpochMilli()
}

class SnowflakeGenerator(
    private val timeSource: Snowflake.TimeSource = DefaultTimeSource
) : Snowflake.Generator {

    override fun next(): Snowflake.Id {
        TODO("Not yet implemented")
    }

}