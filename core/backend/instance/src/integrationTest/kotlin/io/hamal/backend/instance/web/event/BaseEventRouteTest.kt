package io.hamal.backend.instance.web.event

import io.hamal.backend.instance.web.BaseRouteTest
import io.hamal.lib.domain.req.CreateTopicReq
import io.hamal.lib.domain.req.SubmittedAppendEventReq
import io.hamal.lib.domain.req.SubmittedCreateTopicReq
import io.hamal.lib.domain.vo.Content
import io.hamal.lib.domain.vo.ContentType
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.domain.ListEventsResponse
import io.hamal.lib.sdk.domain.ListTopicsResponse
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class BaseEventRouteTest : BaseRouteTest() {

    fun listEvents(topicId: TopicId): ListEventsResponse {
        val listTopicsResponse = httpTemplate.get("/v1/topics/${topicId.value.value}/events").execute()
        assertThat(listTopicsResponse.statusCode, equalTo(HttpStatusCode.Ok))
        require(listTopicsResponse is SuccessHttpResponse) { "request was not successful" }
        return listTopicsResponse.result(ListEventsResponse::class)
    }


    fun listTopics(): ListTopicsResponse {
        val listTopicsResponse = httpTemplate.get("/v1/topics").execute()
        assertThat(listTopicsResponse.statusCode, equalTo(HttpStatusCode.Ok))
        require(listTopicsResponse is SuccessHttpResponse) { "request was not successful" }
        return listTopicsResponse.result(ListTopicsResponse::class)
    }

    fun createTopic(topicName: TopicName): SubmittedCreateTopicReq {
        val createTopicResponse = httpTemplate.post("/v1/topics").body(CreateTopicReq(topicName)).execute()

        assertThat(createTopicResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(createTopicResponse is SuccessHttpResponse) { "request was not successful" }

        return createTopicResponse.result(SubmittedCreateTopicReq::class)
    }

    fun appendEvent(topicId: TopicId, contentType: ContentType, content: Content): SubmittedAppendEventReq {
        val createTopicResponse = httpTemplate.post("/v1/topics/${topicId.value.value}/events")
            .body(contentType.value, content.value)
            .execute()

        assertThat(createTopicResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(createTopicResponse is SuccessHttpResponse) { "request was not successful" }

        return createTopicResponse.result(SubmittedAppendEventReq::class)
    }
}