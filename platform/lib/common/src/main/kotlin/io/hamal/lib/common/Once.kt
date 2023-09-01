package io.hamal.lib.domain

import java.util.concurrent.Semaphore

interface Once<T : Any> {
    operator fun invoke(fn: () -> T): T

    companion object {
        fun <T : Any> default(): Once<T> {
            return DefaultImpl<T>()
        }
    }

    class DefaultImpl<T : Any> : Once<T> {
        private val mutex = Semaphore(1, true)
        private var done = false
        private var result: T? = null

        override fun invoke(fn: () -> T): T {
            if (done) {
                return result!!
            }
            mutex.acquireUninterruptibly()
            if (done) {
                mutex.release()
                return result!!
            }
            try {
                result = fn()
                done = true
            } finally {
                mutex.release()
            }
            return result!!
        }
    }
}
