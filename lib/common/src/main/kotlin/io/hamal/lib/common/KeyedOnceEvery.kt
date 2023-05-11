package io.hamal.lib.common

import io.hamal.lib.domain.OnceEvery
import io.hamal.lib.common.supplier.InstantSupplier
import io.hamal.lib.common.util.LruCache
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration

interface KeyedOnceEvery<KEY : Any, VALUE : Any> {
    operator fun invoke(key: KEY, fn: () -> VALUE): VALUE

    companion object {
        fun <KEY : Any, VALUE : Any> default(
            every: Duration,
            instantSupplier: InstantSupplier = InstantSupplier.default()
        ): KeyedOnceEvery<KEY, VALUE> {
            return DefaultImpl(every, instantSupplier)
        }

        fun <KEY : Any, VALUE : Any> lru(
            capacity: Int,
            every: Duration,
            instantSupplier: InstantSupplier = InstantSupplier.default()
        ): KeyedOnceEvery<KEY, VALUE> {
            return LruImpl(every, instantSupplier, capacity)
        }
    }

    class DefaultImpl<KEY : Any, VALUE : Any>(
        private val every: Duration,
        private val instantSupplier: InstantSupplier
    ) : KeyedOnceEvery<KEY, VALUE> {
        private val onceStore: ConcurrentHashMap<KEY, OnceEvery<VALUE>> = ConcurrentHashMap<KEY, OnceEvery<VALUE>>()

        override fun invoke(key: KEY, fn: () -> VALUE): VALUE {
            onceStore.putIfAbsent(key, OnceEvery.default(every, instantSupplier))
            return onceStore[key]!!.invoke(fn)
        }
    }

    class LruImpl<KEY : Any, VALUE : Any>(
        private val every: Duration,
        private val instantSupplier: InstantSupplier,
        private val maxCapacity: Int
    ) : KeyedOnceEvery<KEY, VALUE> {
        private val onceStore: LruCache<KEY, OnceEvery<VALUE>> = LruCache.DefaultImpl(maxCapacity)

        override fun invoke(key: KEY, fn: () -> VALUE): VALUE {
            onceStore.putIfAbsent(key, OnceEvery.default(every, instantSupplier))
            return onceStore[key].invoke(fn)
        }
    }
}