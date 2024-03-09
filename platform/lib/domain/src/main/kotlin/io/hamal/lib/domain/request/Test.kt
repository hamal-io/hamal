package io.hamal.lib.domain.request

import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.AuthId
import io.hamal.lib.domain.vo.RequestId

data class TestRequested(
    override val requestId: RequestId,
    override val requestedBy: AuthId,
    override var requestStatus: RequestStatus
) : Requested()