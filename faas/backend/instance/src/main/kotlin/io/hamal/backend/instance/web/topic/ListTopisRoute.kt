package io.hamal.backend.instance.web.topic

import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.lib.sdk.domain.ListTopicsResponse
import io.hamal.lib.sdk.domain.ListTopicsResponse.Topic
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ListTopisRoute(
    private val eventBrokerRepository: LogBrokerRepository
) {
    @GetMapping("/v1/topics")
    fun listTopics(): ResponseEntity<ListTopicsResponse> {
        val topics = eventBrokerRepository.listTopics()
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