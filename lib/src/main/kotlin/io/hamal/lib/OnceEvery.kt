package io.hamal.lib

import io.hamal.lib.supplier.InstantSupplier
import java.time.Instant
import java.util.concurrent.Semaphore
import kotlin.time.Duration
import kotlin.time.toJavaDuration

interface OnceEvery<T : Any> {

    operator fun invoke(fn: () -> T): T

    companion object {
        fun <T : Any> default(every: Duration): io.hamal.lib.OnceEvery<T> {
            return io.hamal.lib.OnceEvery.Companion.default(every, InstantSupplier.default())
        }

        fun <T : Any> default(every: Duration, instantSupplier: InstantSupplier): io.hamal.lib.OnceEvery<T> {
            return io.hamal.lib.OnceEvery.DefaultImpl(every, instantSupplier)
        }
    }

    class DefaultImpl<T : Any>(
        private val every: Duration,
        private val instantSupplier: InstantSupplier
    ) : io.hamal.lib.OnceEvery<T> {
        private val mutex = Semaphore(1, true)
        private var nextInvocation = Instant.MIN
        private var result: T? = null

        override fun invoke(fn: () -> T): T {
            val now = instantSupplier()
            if (now.isBefore(nextInvocation)) {
                return result!!
            }
            mutex.acquireUninterruptibly()
            if (now.isBefore(nextInvocation)) {
                mutex.release()
                return result!!
            }
            try {
                result = fn()
                nextInvocation = now.plus(every.toJavaDuration())
            } finally {
                mutex.release()
            }
            return result!!
        }
    }

}