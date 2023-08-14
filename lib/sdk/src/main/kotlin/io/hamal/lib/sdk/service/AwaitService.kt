package io.hamal.lib.sdk.service

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.sdk.domain.ApiSubmittedReq

interface AwaitService {
    fun await(req: ApiSubmittedReq)
}

class DefaultAwaitService(private val template: HttpTemplate) : AwaitService {
    override fun await(req: ApiSubmittedReq) {
        while (true) {
            template.get("/v1/reqs/{reqId}")
                .path("reqId", req.reqId)
                .execute(ApiSubmittedReq::class)
                .let {
                    when (it.status) {
                        ReqStatus.Completed -> {
                            return
                        }

                        ReqStatus.Failed -> {
                            throw IllegalStateException("expected ${req.reqId} to complete but failed")
                        }

                        else -> {
                            Thread.sleep(1)
                        }
                    }
                }
        }
    }
}