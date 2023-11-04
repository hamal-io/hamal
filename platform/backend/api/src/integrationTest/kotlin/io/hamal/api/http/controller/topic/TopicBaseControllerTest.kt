package io.hamal.api.http.controller.topic

import io.hamal.api.http.controller.BaseControllerTest
import io.hamal.lib.domain.vo.TopicEntryPayload
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

    fun listTopicEntries(topicId: TopicId): ApiTopicEntryList {
        val listTopicsResponse = httpTemplate.get("/v1/topics/{topicId}/entries")
            .path("topicId", topicId)
            .execute()

        assertThat(listTopicsResponse.statusCode, equalTo(Ok))
        require(listTopicsResponse is HttpSuccessResponse) { "request was not successful" }
        return listTopicsResponse.result(ApiTopicEntryList::class)
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


    fun createTopic(topicName: TopicName): ApiTopicCreateSubmitted {
        val createTopicResponse = httpTemplate.post("/v1/namespaces/1/topics")
            .body(ApiTopicCreateReq(topicName))
            .execute()

        assertThat(createTopicResponse.statusCode, equalTo(Accepted))
        require(createTopicResponse is HttpSuccessResponse) { "request was not successful" }

        return createTopicResponse.result(ApiTopicCreateSubmitted::class)
    }

    fun appendToTopic(topicId: TopicId, toAppend: TopicEntryPayload): ApiTopicAppendSubmitted {
        val createTopicResponse = httpTemplate.post("/v1/topics/{topicId}/entries")
            .path("topicId", topicId)
            .body(toAppend)
            .execute()

        assertThat(createTopicResponse.statusCode, equalTo(Accepted))
        require(createTopicResponse is HttpSuccessResponse) { "request was not successful" }

        return createTopicResponse.result(ApiTopicAppendSubmitted::class)
    }
}