package io.hamal.request

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.State

interface StateSetReq {
    val correlation: Correlation
    val value: State
}
