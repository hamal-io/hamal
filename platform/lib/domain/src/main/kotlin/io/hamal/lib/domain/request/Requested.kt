package io.hamal.lib.domain.request

import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.RequestId

sealed class Requested {
    abstract val id: RequestId
    abstract var status: RequestStatus
    val type: String = this::class.java.simpleName
}