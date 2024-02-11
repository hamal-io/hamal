package io.hamal.api.http.controller.topic

import io.hamal.api.http.controller.BaseControllerTest
import io.hamal.lib.domain.vo.TopicEventPayload
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class TopicBaseControllerTest : BaseControllerTest() {

    fun listTopicEvents(topicId: TopicId): ApiTopicEventList {
        val listTopicsResponse = httpTemplate.get("/v1/topics/{topicId}/events")
            .path("topicId", topicId)
            .execute()

        assertThat(listTopicsResponse.statusCode, equalTo(Ok))
        require(listTopicsResponse is HttpSuccessResponse) { "request was not successful" }
        return listTopicsResponse.result(ApiTopicEventList::class)
    }


    fun listTopics(
        names: List<TopicName> = listOf()
    ): ApiTopicList {
        val listTopicsResponse = httpTemplate.get("/v1/topics")
            .parameter("names", names.joinToString(",") { it.value })
            .execute()

        assertThat(listTopicsResponse.statusCode, equalTo(Ok))
        require(listTopicsResponse is HttpSuccessResponse) { "request was not successful" }
        return listTopicsResponse.result(ApiTopicList::class)
    }

    fun getTopic(topicId: TopicId): ApiTopic {
        val getTopicResponse = httpTemplate.get("/v1/topics/{topicId}")
            .path("topicId", topicId)
            .execute()

        assertThat(getTopicResponse.statusCode, equalTo(Ok))
        require(getTopicResponse is HttpSuccessResponse) { "request was not successful" }
        return getTopicResponse.result(ApiTopic::class)
    }


    fun createGroupTopic(topicName: TopicName): ApiTopicGroupCreateRequested {
        val createTopicResponse = httpTemplate.post("/v1/groups/1/topics")
            .body(ApiTopicGroupCreateRequest(topicName))
            .execute()

        assertThat(createTopicResponse.statusCode, equalTo(Accepted))
        require(createTopicResponse is HttpSuccessResponse) { "request was not successful" }

        return createTopicResponse.result(ApiTopicGroupCreateRequested::class)
    }

    fun createPublicTopic(topicName: TopicName): ApiTopicPublicCreateRequested {
        val createTopicResponse = httpTemplate.post("/v1/groups/1/public-topics")
            .body(ApiTopicPublicCreateRequest(topicName))
            .execute()

        assertThat(createTopicResponse.statusCode, equalTo(Accepted))
        require(createTopicResponse is HttpSuccessResponse) { "request was not successful" }

        return createTopicResponse.result(ApiTopicPublicCreateRequested::class)
    }

    fun appendToTopic(topicId: TopicId, toAppend: TopicEventPayload): ApiTopicAppendRequested {
        val appendedResponse = httpTemplate.post("/v1/topics/{topicId}/events")
            .path("topicId", topicId)
            .body(toAppend)
            .execute()

        assertThat(appendedResponse.statusCode, equalTo(Accepted))
        require(appendedResponse is HttpSuccessResponse) { "request was not successful" }

        return appendedResponse.result(ApiTopicAppendRequested::class)
    }
}