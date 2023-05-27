package io.hamal.backend.web

import io.hamal.backend.service.cmd.EventCmdService
import io.hamal.backend.service.query.EventQueryService
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.vo.TenantId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.sdk.domain.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
open class EventController @Autowired constructor(
    val cmdService: EventCmdService,
    val queryService: EventQueryService
) {

    @GetMapping("/v1/topics")
    fun listTopics(): ResponseEntity<ApiListTopicResponse> {
        return ResponseEntity.ok(
            ApiListTopicResponse(
                topics = queryService.query {

                }.map { topic ->
                    ApiListTopicResponse.Topic(
                        id = topic.id,
                        name = topic.name
                    )
                }
            )
        )
    }

    @PostMapping("/v1/topics")
    fun createTopic(
        @RequestBody request: ApiCreateTopicRequest
    ): ResponseEntity<ApiCreateTopicResponse> {

        return ResponseEntity.ok(
            with(
                cmdService.create(
                    EventCmdService.TopicToCreate(
                        reqId = ReqId(1),
                        shard = Shard(1),
                        tenantId = TenantId(1),
                        name = request.name
                    )
                )
            ) {
                ApiCreateTopicResponse(
                    id = this.id,
                    name = this.name
                )
            }
        )
    }


    @PostMapping("/v1/topics/{topicId}/events")
    fun appendEvent(
        @PathVariable("topicId") topicId: String,
        @RequestBody body: ApiAppendEvenRequest
    ): ResponseEntity<ApiAppendEventResponse> {

        cmdService.append(
            EventCmdService.EventToAppend(
                reqId = ReqId(1),
                shard = Shard(1),
                tenantId = TenantId(1),
                topicId = TopicId(topicId.toInt()),
                value = "I am an event"
            )
        )

        return ResponseEntity.ok(
            ApiAppendEventResponse(
                topicId = TopicId(1),
                topicName = TopicName("TBD")
            )
        )
    }

    @GetMapping("/v1/topics/{topicId}/events")
    fun listEvents(
        @PathVariable("topicId") topicId: String,
        @RequestParam(required = false, name = "stringEvtId", defaultValue = "0") stringEvtId: String,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Int
    ): ResponseEntity<ApiListEventResponse> {

        TODO()

//        return ResponseEntity.ok(
//            ApiListEventResponse(
//                topic = "SomeTopic",
//                events = listOf()
//            )
//        )
    }

}