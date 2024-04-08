package com.nyanbot.http.controller

import com.nyanbot.repository.FlowRepository
import com.nyanbot.repository.FlowTriggerType
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.sdk.ApiSdkImpl
import io.hamal.lib.sdk.api.ApiTopicService.TopicQuery
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController



data class FlowTriggerRegistryItem(
    val type: FlowTriggerType,
    val topicName: TopicName
)

@RestController
class TestController(
    private val flowRepository: FlowRepository,
    @Value("\${io.hamal.server.url}") val url: String,
    @Value("\${io.hamal.server.token}") val token: String
) {

    @PostMapping("/v1/test")
    fun invoke() {

        val sdk = ApiSdkImpl(HttpTemplateImpl(
            baseUrl = url,
            headerFactory = { this["Authorization"] = "Bearer $token" }
        ))

        val topics = sdk.topic.list(
            TopicQuery(
                limit = Limit(200)
            )
        )


        topics.forEach(::println)

    }

}