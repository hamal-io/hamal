package io.hamal.lib.domain

import io.hamal.lib.domain.util.LruCache
import java.util.concurrent.ConcurrentHashMap

interface KeyedOnce<KEY : Any, VALUE : Any> {
    operator fun invoke(key: KEY, fn: () -> VALUE): VALUE

    fun keys(): Set<KEY>

    companion object {
        fun <KEY : Any, VALUE : Any> default(): KeyedOnce<KEY, VALUE> {
            return DefaultImpl()
        }

        fun <KEY : Any, VALUE : Any> lru(capacity: Int): KeyedOnce<KEY, VALUE> {
            return LruImpl(capacity)
        }
    }

    class DefaultImpl<KEY : Any, VALUE : Any> : KeyedOnce<KEY, VALUE> {
        private val onceStore: ConcurrentHashMap<KEY, Once<VALUE>> = ConcurrentHashMap<KEY, Once<VALUE>>()

        override fun invoke(key: KEY, fn: () -> VALUE): VALUE {
            onceStore.putIfAbsent(key, Once.default())
            return onceStore[key]!!.invoke(fn)
        }

        override fun keys(): Set<KEY> {
            return onceStore.keys
        }
    }

    class LruImpl<KEY : Any, VALUE : Any>(maxCapacity: Int) : KeyedOnce<KEY, VALUE> {
        private val onceStore: LruCache<KEY, Once<VALUE>> = LruCache.DefaultImpl(maxCapacity)

        override fun invoke(key: KEY, fn: () -> VALUE): VALUE {
            onceStore.putIfAbsent(key, Once.default())
            return onceStore[key].invoke(fn)
        }

        override fun keys(): Set<KEY> {
            return onceStore.keys()
        }
    }
}