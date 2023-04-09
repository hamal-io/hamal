package io.hamal.module.bus.api.producer

import io.hamal.module.bus.api.common.Record

interface Producer<KEY : Any, VALUE : Any> {
    operator fun invoke(records: List<Record<KEY, VALUE>>)
}