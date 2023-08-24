package io.hamal.backend.instance.web.topic

import io.hamal.backend.instance.web.BaseRouteTest
import io.hamal.lib.domain.req.CreateTopicReq
import io.hamal.lib.domain.vo.EventPayload
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.domain.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class BaseTopicRouteTest : BaseRouteTest() {

    fun listTopicEvents(topicId: TopicId): ListEventsResponse {
        val listTopicsResponse = httpTemplate.get("/v1/topics/{topicId}/events")
            .path("topicId", topicId)
            .execute()

        assertThat(listTopicsResponse.statusCode, equalTo(Ok))
        require(listTopicsResponse is SuccessHttpResponse) { "request was not successful" }
        return listTopicsResponse.result(ListEventsResponse::class)
    }


    fun listTopics(
        names: List<TopicName> = listOf()
    ): ApiTopicList {
        val listTopicsResponse = httpTemplate.get("/v1/topics")
            .parameter("names", names.joinToString(",") { it.value })
            .execute()

        assertThat(listTopicsResponse.statusCode, equalTo(Ok))
        require(listTopicsResponse is SuccessHttpResponse) { "request was not successful" }
        return listTopicsResponse.result(ApiTopicList::class)
    }

    fun getTopic(topicId: TopicId): ApiTopic {
        val getTopicResponse = httpTemplate.get("/v1/topics/{topicId}")
            .path("topicId", topicId)
            .execute()

        assertThat(getTopicResponse.statusCode, equalTo(Ok))
        require(getTopicResponse is SuccessHttpResponse) { "request was not successful" }
        return getTopicResponse.result(ApiTopic::class)
    }


    fun createTopic(topicName: TopicName): ApiSubmittedReqWithId {
        val createTopicResponse = httpTemplate.post("/v1/topics").body(CreateTopicReq(topicName)).execute()

        assertThat(createTopicResponse.statusCode, equalTo(Accepted))
        require(createTopicResponse is SuccessHttpResponse) { "request was not successful" }

        return createTopicResponse.result(ApiSubmittedReqWithId::class)
    }

    fun appendToTopic(topicId: TopicId, toAppend: EventPayload): ApiSubmittedReq {
        val createTopicResponse = httpTemplate.post("/v1/topics/{topicId}/events")
            .path("topicId", topicId)
            .body(toAppend)
            .execute()

        assertThat(createTopicResponse.statusCode, equalTo(Accepted))
        require(createTopicResponse is SuccessHttpResponse) { "request was not successful" }

        return createTopicResponse.result(ApiDefaultSubmittedReq::class)
    }
}