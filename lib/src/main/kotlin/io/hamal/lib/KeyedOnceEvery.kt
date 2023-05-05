package io.hamal.lib

import io.hamal.lib.supplier.InstantSupplier
import io.hamal.lib.util.LruCache
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration

interface KeyedOnceEvery<KEY : Any, VALUE : Any> {
    operator fun invoke(key: KEY, fn: () -> VALUE): VALUE

    companion object {
        fun <KEY : Any, VALUE : Any> default(
            every: Duration,
            instantSupplier: InstantSupplier = InstantSupplier.default()
        ): io.hamal.lib.KeyedOnceEvery<KEY, VALUE> {
            return io.hamal.lib.KeyedOnceEvery.DefaultImpl(every, instantSupplier)
        }

        fun <KEY : Any, VALUE : Any> lru(
            capacity: Int,
            every: Duration,
            instantSupplier: InstantSupplier = InstantSupplier.default()
        ): io.hamal.lib.KeyedOnceEvery<KEY, VALUE> {
            return io.hamal.lib.KeyedOnceEvery.LruImpl(every, instantSupplier, capacity)
        }
    }

    class DefaultImpl<KEY : Any, VALUE : Any>(
        private val every: Duration,
        private val instantSupplier: InstantSupplier
    ) : io.hamal.lib.KeyedOnceEvery<KEY, VALUE> {
        private val onceStore: ConcurrentHashMap<KEY, io.hamal.lib.OnceEvery<VALUE>> = ConcurrentHashMap<KEY, io.hamal.lib.OnceEvery<VALUE>>()

        override fun invoke(key: KEY, fn: () -> VALUE): VALUE {
            onceStore.putIfAbsent(key, io.hamal.lib.OnceEvery.Companion.default(every, instantSupplier))
            return onceStore[key]!!.invoke(fn)
        }
    }

    class LruImpl<KEY : Any, VALUE : Any>(
        private val every: Duration,
        private val instantSupplier: InstantSupplier,
        private val maxCapacity: Int
    ) : io.hamal.lib.KeyedOnceEvery<KEY, VALUE> {
        private val onceStore: LruCache<KEY, io.hamal.lib.OnceEvery<VALUE>> = LruCache.DefaultImpl(maxCapacity)

        override fun invoke(key: KEY, fn: () -> VALUE): VALUE {
            onceStore.putIfAbsent(key, io.hamal.lib.OnceEvery.Companion.default(every, instantSupplier))
            return onceStore[key].invoke(fn)
        }
    }
}