package io.hamal.lib.common

import io.hamal.lib.domain.Once
import java.util.concurrent.ConcurrentHashMap

interface KeyedOnce<KEY : Any, VALUE : Any> {
    operator fun invoke(key: KEY, fn: (key: KEY) -> VALUE): VALUE

    fun keys(): Set<KEY>

    fun clear()

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

        override fun invoke(key: KEY, fn: (key: KEY) -> VALUE): VALUE {
            onceStore.putIfAbsent(key, Once.default())
            return onceStore[key]!!{ fn(key) }
        }

        override fun keys(): Set<KEY> {
            return onceStore.keys
        }

        override fun clear() {
            onceStore.clear()
        }
    }

    class LruImpl<KEY : Any, VALUE : Any>(maxCapacity: Int) : KeyedOnce<KEY, VALUE> {
        private val onceStore: LruCache<KEY, Once<VALUE>> = DefaultLruCache(maxCapacity)

        override fun invoke(key: KEY, fn: (key: KEY) -> VALUE): VALUE {
            onceStore.putIfAbsent(key, Once.default())
            return onceStore[key]{ fn(key) }
        }

        override fun keys(): Set<KEY> {
            return onceStore.keys()
        }

        override fun clear() {
            onceStore.clear()
        }
    }
}