package io.hamal.lib.domain.request

import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.AuthId
import io.hamal.lib.domain.vo.RequestId

data class TestRequested(
    override val id: RequestId,
    override val by: AuthId,
    override var status: RequestStatus
) : Requested()