package io.hamal.lib.sdk.api

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.http.HttpTemplate

interface ApiAwaitService {
    operator fun invoke(req: ApiSubmitted) = await(req)

    fun await(req: ApiSubmitted)
}

internal class ApiAwaitServiceImpl(
    private val template: HttpTemplate
) : ApiAwaitService {
    override fun await(req: ApiSubmitted) {
        while (true) {
            template.get("/v1/reqs/{reqId}")
                .path("reqId", req.id)
                .execute(ApiSubmitted::class)
                .let {
                    when (it.status) {
                        ReqStatus.Completed -> {
                            return
                        }

                        ReqStatus.Failed -> {
                            throw IllegalStateException("expected ${req.id} to complete but failed")
                        }

                        else -> {
                            Thread.sleep(1)
                        }
                    }
                }
        }
    }
}