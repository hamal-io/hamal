package io.hamal.backend.web

import io.hamal.backend.repository.api.domain.tenant.Tenant
import io.hamal.backend.service.cmd.TriggerCmdService
import io.hamal.backend.service.cmd.TriggerCmdService.TriggerToCreate
import io.hamal.backend.service.query.TriggerQueryService
import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.sdk.domain.ApiCreateTriggerRequest
import io.hamal.lib.sdk.domain.ApiCreateTriggerResponse
import io.hamal.lib.sdk.domain.ApiListTriggerResponse
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import kotlin.time.*


@RestController
open class TriggerController(
    @Autowired val queryService: TriggerQueryService,
    @Autowired val cmdService: TriggerCmdService
) {
    @PostMapping("/v1/triggers")
    fun createTrigger(
        @RequestAttribute shard: Shard,
        @RequestAttribute reqId: ReqId,
        @RequestAttribute tenant: Tenant,
        @RequestBody req: ApiCreateTriggerRequest
    ): ResponseEntity<ApiCreateTriggerResponse> {


        val result = cmdService.create(
            TriggerToCreate(
                reqId = reqId,
                shard = shard,
                name = req.name,
                funcId = req.funcId
            )
        )

        return ResponseEntity(
            ApiCreateTriggerResponse(
                id = result.id,
                name = result.name,
            ),
            HttpStatus.CREATED
        )
    }


    @GetMapping("/v1/triggers")
    fun listTrigger(
        @RequestParam(required = false, name = "after_id", defaultValue = "0") stringTriggerId: String,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Int
    ): ResponseEntity<ApiListTriggerResponse> {
        val result =
            queryService.list(
                afterId = TriggerId(SnowflakeId(stringTriggerId.toLong())),
                limit = limit
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
