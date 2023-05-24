package io.hamal.backend.infra.web

import io.hamal.backend.core.tenant.Tenant
import io.hamal.backend.infra.usecase.query.TriggerQuery
import io.hamal.backend.infra.usecase.request.TriggerRequest
import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.ddd.InvokeQueryManyUseCasePort
import io.hamal.lib.domain.ddd.InvokeRequestOneUseCasePort
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.sdk.domain.ApiCreateTriggerRequest
import io.hamal.lib.sdk.domain.ApiCreateTriggerResponse
import io.hamal.lib.sdk.domain.ApiListTriggerResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
open class TriggerController(
    @Autowired val queryMany: InvokeQueryManyUseCasePort,
    @Autowired val request: InvokeRequestOneUseCasePort,
) {
    @PostMapping("/v1/triggers")
    fun createTrigger(
        @RequestAttribute shard: Shard,
        @RequestAttribute reqId: ReqId,
        @RequestAttribute tenant: Tenant,
        @RequestBody req: ApiCreateTriggerRequest
    ): ResponseEntity<ApiCreateTriggerResponse> {
        val result = request(
            TriggerRequest.TriggerCreation(
                reqId = reqId,
                shard = shard,
                name = req.name,
                code = req.code
            )
        )

        return ResponseEntity(
            ApiCreateTriggerResponse(
                id = result.id,
                name = result.name,
                code = result.code
            ),
            HttpStatus.CREATED
        )
    }


    @GetMapping("/v1/triggers")
    fun listTrigger(
        @RequestParam(required = false, name = "after_id", defaultValue = "0") stringTriggerId: String,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Int
    ): ResponseEntity<ApiListTriggerResponse> {
        val result = queryMany(
            io.hamal.backend.infra.usecase.query.TriggerQuery.ListTrigger(
                afterId = TriggerId(SnowflakeId(stringTriggerId.toLong())),
                limit = limit
            )
        )

        return ResponseEntity.ok(
            ApiListTriggerResponse(
                result.map {
                    ApiListTriggerResponse.Trigger(
                        id = it.id,
                        name = it.name
                    )
                }
            )
        )
    }
}