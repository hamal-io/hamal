package io.hamal.lib.common

import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

// FIXME needs a complete overhaul
interface LruCache<KEY : Any, VALUE : Any> {

    operator fun set(key: KEY, value: VALUE)

    fun setIfAbsent(key: KEY, value: VALUE)

    fun find(key: KEY): VALUE?

    operator fun get(key: KEY): VALUE {
        return find(key) ?: throw IllegalArgumentException("$key")
    }

    fun computeIfAbsent(key: KEY, mapper: (KEY) -> VALUE?): VALUE?

    fun size(): Int

    fun keys(): Set<KEY>

    fun clear()
}

class DefaultLruCache<KEY : Any, VALUE : Any>(capacity: Int) : LruCache<KEY, VALUE> {
    private var store: LinkedHashMap<KEY, VALUE>
    private val lock: ReadWriteLock = ReentrantReadWriteLock()

    init {
        store = object : LinkedHashMap<KEY, VALUE>(capacity, 0.75f, true) {
            override fun removeEldestEntry(eldest: Map.Entry<KEY, VALUE>): Boolean {
                return size > capacity
            }
        }
    }

    override fun set(key: KEY, value: VALUE) {
        lock.writeLock().withLock {
            store[key] = value
        }
    }

    override fun setIfAbsent(key: KEY, value: VALUE) {
        lock.writeLock().withLock {
            store.putIfAbsent(key, value)
        }
    }

    override fun find(key: KEY): VALUE? {
        return lock.readLock().withLock {
            store[key]
        }
    }

    override fun computeIfAbsent(key: KEY, mapper: (KEY) -> VALUE?): VALUE? {
        return lock.writeLock().withLock {
            if (!store.containsKey(key)) {
                mapper(key)?.also { store[key] = it }
            } else {
                store[key]
            }
        }
    }


    override fun size(): Int {
        return lock.readLock()
            .withLock {
                store.size
            }
    }

    override fun keys(): Set<KEY> {
        return lock.readLock().withLock { store.keys }
    }

    override fun clear() {
        lock.writeLock()
            .withLock {
                store.clear()
            }
    }
}