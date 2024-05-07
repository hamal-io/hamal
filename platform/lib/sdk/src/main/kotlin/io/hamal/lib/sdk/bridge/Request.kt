package io.hamal.lib.sdk.bridge

import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.domain.vo.RequestStatus

sealed class BridgeRequested {
    abstract val id: RequestId
    abstract val status: RequestStatus
    val type: String = this::class.java.simpleName
}
