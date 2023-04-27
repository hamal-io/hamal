package io.hamal.lib

import io.hamal.lib.util.LruCache
import java.util.concurrent.ConcurrentHashMap

interface KeyedOnce<KEY : Any, VALUE : Any> {
    operator fun invoke(key: KEY, fn: () -> VALUE): VALUE

    fun keys(): Set<KEY>

    companion object {
        fun <KEY : Any, VALUE : Any> default(): io.hamal.lib.KeyedOnce<KEY, VALUE> {
            return io.hamal.lib.KeyedOnce.DefaultImpl()
        }

        fun <KEY : Any, VALUE : Any> lru(capacity: Int): io.hamal.lib.KeyedOnce<KEY, VALUE> {
            return io.hamal.lib.KeyedOnce.LruImpl(capacity)
        }
    }

    class DefaultImpl<KEY : Any, VALUE : Any> : io.hamal.lib.KeyedOnce<KEY, VALUE> {
        private val onceStore: ConcurrentHashMap<KEY, io.hamal.lib.Once<VALUE>> = ConcurrentHashMap<KEY, io.hamal.lib.Once<VALUE>>()

        override fun invoke(key: KEY, fn: () -> VALUE): VALUE {
            onceStore.putIfAbsent(key, io.hamal.lib.Once.Companion.default())
            return onceStore[key]!!.invoke(fn)
        }

        override fun keys(): Set<KEY> {
            return onceStore.keys
        }
    }

    class LruImpl<KEY : Any, VALUE : Any>(maxCapacity: Int) : io.hamal.lib.KeyedOnce<KEY, VALUE> {
        private val onceStore: LruCache<KEY, io.hamal.lib.Once<VALUE>> = LruCache.DefaultImpl(maxCapacity)

        override fun invoke(key: KEY, fn: () -> VALUE): VALUE {
            onceStore.putIfAbsent(key, io.hamal.lib.Once.Companion.default())
            return onceStore[key].invoke(fn)
        }

        override fun keys(): Set<KEY> {
            return onceStore.keys()
        }
    }
}