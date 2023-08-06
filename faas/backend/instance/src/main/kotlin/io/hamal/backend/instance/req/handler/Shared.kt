package io.hamal.backend.instance.req.handler

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.req.SubmittedReq

internal fun SubmittedReq.cmdId() = CmdId(reqId.value.value)