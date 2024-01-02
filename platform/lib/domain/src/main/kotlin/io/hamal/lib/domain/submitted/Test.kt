package io.hamal.lib.domain.submitted

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.ReqId

data class TestSubmitted(
    override val id: ReqId,
    override var status: ReqStatus
) : Submitted()