package io.hamal.module.bus.api.consumer

import io.hamal.module.bus.api.common.Record

interface Consumer<KEY : Any, VALUE : Any> {
    operator fun invoke(): List<Record<KEY, VALUE>>
}