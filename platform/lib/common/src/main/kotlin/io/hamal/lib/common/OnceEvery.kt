package io.hamal.lib.domain

import io.hamal.lib.common.supplier.InstantSupplier
import java.time.Instant
import java.util.concurrent.Semaphore
import kotlin.time.Duration
import kotlin.time.toJavaDuration

interface OnceEvery<T : Any> {

    operator fun invoke(fn: () -> T): T

    companion object {
        fun <T : Any> default(every: Duration): OnceEvery<T> {
            return default(every, InstantSupplier.default())
        }

        fun <T : Any> default(every: Duration, instantSupplier: InstantSupplier): OnceEvery<T> {
            return DefaultImpl(every, instantSupplier)
        }
    }

    class DefaultImpl<T : Any>(
        private val every: Duration,
        private val instantSupplier: InstantSupplier
    ) : OnceEvery<T> {
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