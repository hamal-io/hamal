package io.hamal.api.req.handler

import io.hamal.repository.api.submitted_req.SubmittedReq
import io.hamal.lib.common.domain.CmdId

internal fun SubmittedReq.cmdId() = CmdId(reqId.value.value)