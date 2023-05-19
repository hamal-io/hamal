package io.hamal.backend.infra.web

import io.hamal.backend.core.func.Func
import io.hamal.backend.core.tenant.Tenant
import io.hamal.backend.usecase.request.FuncRequest
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.ddd.InvokeUseCasePort
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RestController

@RestController
open class FuncController(
    @Autowired val request: InvokeUseCasePort,
) {
    @PostMapping("/v1/funcs")
    fun createFunc(
        @RequestAttribute shard: Shard,
        @RequestAttribute reqId: ReqId,
        @RequestAttribute tenant: Tenant,
    ): Func {
        return request(
            FuncRequest.FuncCreation(
                reqId, shard
            )
        )
    }
}