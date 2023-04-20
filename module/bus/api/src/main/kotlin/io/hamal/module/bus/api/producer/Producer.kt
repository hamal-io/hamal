package io.hamal.module.bus.api.producer

import io.hamal.module.bus.api.common.Message

interface Producer<KEY : Any, VALUE : Any> {
    operator fun invoke(records: List<Message<KEY, VALUE>>)
}