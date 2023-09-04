package io.hamal.lib.sdk.hub

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.http.HttpTemplate

interface HubAwaitService {
    operator fun invoke(req: HubSubmittedReq) = await(req)

    fun await(req: HubSubmittedReq)
}

internal class DefaultHubAwaitService(
    private val template: HttpTemplate
) : HubAwaitService {
    override fun await(req: HubSubmittedReq) {
        while (true) {
            template.get("/v1/reqs/{reqId}")
                .path("reqId", req.reqId)
                .execute(HubSubmittedReq::class)
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