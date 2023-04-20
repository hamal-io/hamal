package io.hamal.module.bus.api.consumer

import io.hamal.module.bus.api.common.Message

interface Consumer<KEY : Any, VALUE : Any> {
    operator fun invoke(): List<Message<KEY, VALUE>>
}