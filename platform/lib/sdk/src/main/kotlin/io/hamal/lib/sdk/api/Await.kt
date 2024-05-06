package io.hamal.lib.sdk.api

import io.hamal.lib.domain._enum.RequestStatuses.Completed
import io.hamal.lib.domain._enum.RequestStatuses.Failed
import io.hamal.lib.http.HttpTemplate

interface ApiAwaitService {
    operator fun invoke(req: ApiRequested) = await(req)

    fun await(req: ApiRequested)
}

internal class ApiAwaitServiceImpl(
    private val template: HttpTemplate
) : ApiAwaitService {
    override fun await(req: ApiRequested) {
        while (true) {
            template.get("/v1/requests/{reqId}")
                .path("reqId", req.requestId)
                .execute(ApiRequested::class)
                .let {
                    when (it.requestStatus.enumValue) {
                        Completed -> {
                            return
                        }

                        Failed -> {
                            throw IllegalStateException("expected ${req.requestId} to complete but failed")
                        }

                        else -> {
                            Thread.sleep(1)
                        }
                    }
                }
        }
    }
}