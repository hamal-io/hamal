//package io.hamal.lib.meta
//
//import io.hamal.lib.meta.util.LRUCache
//import java.util.concurrent.ConcurrentHashMap
//import java.util.function.Supplier
//
//interface KeyedOnce<KEY : Any, VALUE : Any> {
//
//    fun run(key: KEY, supplier: () -> VALUE): VALUE
//
//    class DefaultImpl<KEY : Any, VALUE : Any> internal constructor() : KeyedOnce<KEY, VALUE> {
//        private val onceStore: ConcurrentHashMap<KEY, Once<VALUE>> = ConcurrentHashMap<KEY, Once<VALUE>>()
//        override fun run(key: KEY, supplier: Supplier<VALUE>?): VALUE {
//            onceStore.putIfAbsent(key, Once.newInstance())
//            return onceStore[key].run(supplier)
//        }
//    }
//
//    class LRUImpl<KEY, VALUE> internal constructor(maxCapacity: Int) : KeyedOnce<KEY, VALUE> {
//        private val onceCache: LRUCache<KEY, Once<VALUE>>
//
//        init {
//            onceCache = DefaultImpl(maxCapacity)
//        }
//
//        override fun run(key: KEY, supplier: Supplier<VALUE>?): VALUE {
//            onceCache.putIfAbsent(key, Once.newInstance())
//            return onceCache.get(key).run(supplier)
//        }
//    }
//
//    companion object {
//        fun <KEY, VALUE> newInstance(): KeyedOnce<KEY, VALUE>? {
//            return DefaultImpl()
//        }
//
//        fun <KEY, VALUE> newInstance(maxCapacity: Int): KeyedOnce<KEY, VALUE>? {
//            return LRUImpl(maxCapacity)
//        }
//    }
//}
