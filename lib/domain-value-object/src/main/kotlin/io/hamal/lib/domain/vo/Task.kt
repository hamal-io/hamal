package io.hamal.lib.domain.vo

import io.hamal.lib.domain.vo.base.Id

class TaskId(override val value: Value) : Id() {
    constructor(value: String) : this(Value(value))
}
