package io.hamal.lib.common.util

import java.time.Instant

object TimeUtils {
    fun now(): Instant = injectedNow.get() ?: Instant.now()

    internal val injectedNow: ThreadLocal<Instant> = ThreadLocal.withInitial { null }

    fun <T> withEpochMilli(millis: Long, fn: () -> T): T {
        return withInstant(Instant.ofEpochMilli(millis), fn)
    }

    fun <T> withInstant(instant: Instant, fn: () -> T): T {
        injectedNow.set(instant)
        try {
            return fn()
        } finally {
            injectedNow.remove()
        }
    }
}


