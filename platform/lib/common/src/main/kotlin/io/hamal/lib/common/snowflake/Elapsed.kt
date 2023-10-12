package io.hamal.lib.common.snowflake

import java.time.Instant
import kotlin.time.Duration.Companion.milliseconds

@JvmInline
value class Elapsed(val value: Long) : Comparable<Elapsed> {
    override fun compareTo(other: Elapsed) = value.compareTo(other.value)
}

interface ElapsedSource {
    fun elapsed(): Elapsed
}

@OptIn(kotlin.time.ExperimentalTime::class)
class ElapsedSourceImpl(
    val epoch: Long = 1697125891520
) : ElapsedSource {
    override fun elapsed() =
        Elapsed(Instant.now().toEpochMilli().milliseconds.minus(epoch.milliseconds).inWholeMilliseconds)
}

class FixedElapsedSource(val value: Long) : ElapsedSource {
    override fun elapsed() = Elapsed(value)
}