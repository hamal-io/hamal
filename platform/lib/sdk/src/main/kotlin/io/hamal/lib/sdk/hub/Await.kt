package io.hamal.lib.sdk.hub

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.http.HttpTemplate

interface AwaitService {
    operator fun invoke(req: ApiSubmittedReq) = await(req)

    fun await(req: ApiSubmittedReq)
}

internal class DefaultAwaitService(
    private val template: HttpTemplate
) : AwaitService {
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