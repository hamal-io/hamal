package io.hamal.backend.web

import io.hamal.backend.service.cmd.EventCmdService
import io.hamal.backend.service.query.EventQueryService
import io.hamal.lib.sdk.domain.ApiAppendEvenRequest
import io.hamal.lib.sdk.domain.ApiListEventResponse
import io.hamal.lib.sdk.domain.ApiListTopicResponse
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

    @PostMapping("/v1/events/{topicId}")
    fun appendEvent(
        @PathVariable("topicId") topicId: String,
        @RequestBody body: ApiAppendEvenRequest
    ): ResponseEntity<ApiListEventResponse> {

        TODO()

//        return ResponseEntity.ok(
//            ApiListEventResponse(
//                topic = "SomeTopic",
//                events = listOf()
//            )
//        )
    }

    @GetMapping("/v1/events/{topicId}")
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