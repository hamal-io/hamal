package io.hamal.lib

import java.util.concurrent.Semaphore

interface Once<T : Any> {
    operator fun invoke(fn: () -> T): T

    companion object {
        fun <T : Any> default(): io.hamal.lib.Once<T> {
            return io.hamal.lib.Once.DefaultImpl<T>()
        }
    }

    class DefaultImpl<T : Any> : io.hamal.lib.Once<T> {
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
