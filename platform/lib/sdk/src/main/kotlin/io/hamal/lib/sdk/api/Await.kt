package io.hamal.lib.sdk.api

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.http.HttpTemplate

interface ApiAwaitService {
    operator fun invoke(req: ApiSubmittedReq) = await(req)

    fun await(req: ApiSubmittedReq)
}

internal class ApiAwaitServiceImpl(
    private val template: HttpTemplate
) : ApiAwaitService {
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