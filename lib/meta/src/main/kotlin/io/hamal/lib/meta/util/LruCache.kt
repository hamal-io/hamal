package io.hamal.lib.meta.util

import io.hamal.lib.meta.Maybe
import io.hamal.lib.meta.exception.NotFoundException
import io.hamal.lib.meta.orElseThrow
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock

interface LruCache<KEY : Any, VALUE : Any> {

    fun put(key: KEY, value: VALUE)

    fun putIfAbsent(key: KEY, value: VALUE)

    fun find(key: KEY): Maybe<VALUE>

    operator fun get(key: KEY): VALUE {
        return find(key).orElseThrow { NotFoundException("$key") }
    }

    fun computeIfAbsent(key: KEY, mapper: (KEY) -> VALUE): VALUE

    fun size(): Int

    class DefaultImpl<KEY : Any, VALUE : Any>(capacity: Int) : LruCache<KEY, VALUE> {
        private var store: LinkedHashMap<KEY, VALUE>
        private val lock: ReadWriteLock = ReentrantReadWriteLock()

        init {
            store = object : LinkedHashMap<KEY, VALUE>(capacity, 0.75f, true) {
                override fun removeEldestEntry(eldest: Map.Entry<KEY, VALUE>): Boolean {
                    return size > capacity
                }
            }
        }

        override fun put(key: KEY, value: VALUE) {
            lock.writeLock().lock()
            try {
                store[key] = value
            } finally {
                lock.writeLock().unlock()
            }
        }

        override fun putIfAbsent(key: KEY, value: VALUE) {
            lock.writeLock().lock()
            try {
                store.putIfAbsent(key, value)
            } finally {
                lock.writeLock().unlock()
            }
        }

        override fun find(key: KEY): Maybe<VALUE> {
            lock.readLock().lock()
            return try {
                Maybe(store[key])
            } finally {
                lock.readLock().unlock()
            }
        }

        override fun computeIfAbsent(key: KEY, mapper: (KEY) -> VALUE): VALUE {
            lock.writeLock().lock()
            try {
                return store.computeIfAbsent(key, mapper)
            } finally {
                lock.writeLock().unlock()
            }
        }


        override fun size(): Int {
            lock.readLock().lock()
            return try {
                store.size
            } finally {
                lock.readLock().unlock()
            }
        }
    }

}