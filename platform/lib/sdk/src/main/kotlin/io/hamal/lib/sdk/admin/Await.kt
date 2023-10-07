package io.hamal.lib.sdk.admin

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.http.HttpTemplate

interface AdminAwaitService {
    operator fun invoke(req: AdminSubmittedReq) = await(req)

    fun await(req: AdminSubmittedReq)
}

internal class AdminAwaitServiceImpl(
    private val template: HttpTemplate
) : AdminAwaitService {
    override fun await(req: AdminSubmittedReq) {
        while (true) {
            template.get("/v1/reqs/{reqId}")
                .path("reqId", req.reqId)
                .execute(AdminSubmittedReq::class)
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