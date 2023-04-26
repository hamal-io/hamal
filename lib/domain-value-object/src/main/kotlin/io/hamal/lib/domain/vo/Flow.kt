package io.hamal.lib.domain.vo

import io.hamal.lib.domain.vo.base.Id

class FlowId(override val value: Value) : Id() {
    constructor(value: String) : this(Value(value))
}