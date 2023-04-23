package io.hamal.lib.log.producer

import io.hamal.lib.log.producer.Producer.Record
import kotlin.reflect.KClass

interface Producer<KEY : Any, VALUE : Any> {

    fun produce(record: Record<KEY, VALUE>)

    data class Record<KEY : Any, VALUE : Any>(
        val key: KEY,
        val keyCLass: KClass<KEY>,
        val value: VALUE,
        val valueClass: KClass<VALUE>,
    )
}

class DefaultProducer<KEY : Any, VALUE : Any> : Producer<KEY, VALUE> {
    override fun produce(record: Record<KEY, VALUE>) {
        TODO("Not yet implemented")
    }

}

