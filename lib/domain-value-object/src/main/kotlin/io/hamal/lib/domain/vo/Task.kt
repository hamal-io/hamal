package io.hamal.lib.domain.vo

import io.hamal.lib.domain.vo.base.Id
import kotlinx.serialization.Serializable

@Serializable
class TaskId(override val value: Value) : Id() {
    constructor(value: String) : this(Value(value))
}
