package io.hamal.backend.instance.web.event

import io.hamal.backend.instance.service.query.EventQueryService
import io.hamal.lib.sdk.domain.*
import io.hamal.lib.sdk.domain.ListTopicsResponse.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
open class ListTopicsRoute(
    private val queryService: EventQueryService<*>
) {
    @GetMapping("/v1/topics")
    fun listTopics(): ResponseEntity<ListTopicsResponse> {
        val topics = queryService.listTopics { }
        return ResponseEntity.ok(
            ListTopicsResponse(
                topics = topics.map { topic ->
                    Topic(
                        id = topic.id,
                        name = topic.name
                    )
                }
            )
        )
    }
}