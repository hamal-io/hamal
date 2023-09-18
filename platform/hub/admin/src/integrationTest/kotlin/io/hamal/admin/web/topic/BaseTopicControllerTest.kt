package io.hamal.admin.web.topic

import io.hamal.admin.web.BaseControllerTest
import io.hamal.lib.domain.vo.TopicEntryPayload
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.hub.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class BaseTopicControllerTest : BaseControllerTest() {

    fun listTopicEntries(topicId: TopicId): HubTopicEntryList {
        val listTopicsResponse = httpTemplate.get("/v1/topics/{topicId}/entries")
            .path("topicId", topicId)
            .execute()

        assertThat(listTopicsResponse.statusCode, equalTo(Ok))
        require(listTopicsResponse is SuccessHttpResponse) { "request was not successful" }
        return listTopicsResponse.result(HubTopicEntryList::class)
    }


    fun listTopics(
        names: List<TopicName> = listOf()
    ): HubTopicList {
        val listTopicsResponse = httpTemplate.get("/v1/groups/{groupId}/topics")
            .path("groupId", testGroup.id)
            .parameter("names", names.joinToString(",") { it.value })
            .execute()

        assertThat(listTopicsResponse.statusCode, equalTo(Ok))
        require(listTopicsResponse is SuccessHttpResponse) { "request was not successful" }
        return listTopicsResponse.result(HubTopicList::class)
    }

    fun getTopic(topicId: TopicId): HubTopic {
        val getTopicResponse = httpTemplate.get("/v1/topics/{topicId}")
            .path("topicId", topicId)
            .execute()

        assertThat(getTopicResponse.statusCode, equalTo(Ok))
        require(getTopicResponse is SuccessHttpResponse) { "request was not successful" }
        return getTopicResponse.result(HubTopic::class)
    }


    fun createTopic(topicName: TopicName): HubSubmittedReqWithId {
        val createTopicResponse = httpTemplate.post("/v1/groups/{groupId}/topics")
            .path("groupId", testGroup.id)
            .body(HubCreateTopicReq(topicName))
            .execute()

        assertThat(createTopicResponse.statusCode, equalTo(Accepted))
        require(createTopicResponse is SuccessHttpResponse) { "request was not successful" }

        return createTopicResponse.result(HubSubmittedReqWithId::class)
    }

    fun appendToTopic(topicId: TopicId, toAppend: TopicEntryPayload): HubSubmittedReq {
        val createTopicResponse = httpTemplate.post("/v1/topics/{topicId}/entries")
            .path("topicId", topicId)
            .body(toAppend)
            .execute()

        assertThat(createTopicResponse.statusCode, equalTo(Accepted))
        require(createTopicResponse is SuccessHttpResponse) { "request was not successful" }

        return createTopicResponse.result(HubDefaultSubmittedReq::class)
    }
}