package io.hamal.admin.web.topic

import io.hamal.admin.web.BaseControllerTest
import io.hamal.lib.domain.vo.TopicEntryPayload
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.admin.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class BaseTopicControllerTest : BaseControllerTest() {

    fun listTopicEntries(topicId: TopicId): AdminTopicEntryList {
        val listTopicsResponse = httpTemplate.get("/v1/topics/{topicId}/entries")
            .path("topicId", topicId)
            .execute()

        assertThat(listTopicsResponse.statusCode, equalTo(Ok))
        require(listTopicsResponse is SuccessHttpResponse) { "request was not successful" }
        return listTopicsResponse.result(AdminTopicEntryList::class)
    }


    fun listTopics(
        names: List<TopicName> = listOf()
    ): AdminTopicList {
        val listTopicsResponse = httpTemplate.get("/v1/groups/{groupId}/topics")
            .path("groupId", testGroup.id)
            .parameter("names", names.joinToString(",") { it.value })
            .execute()

        assertThat(listTopicsResponse.statusCode, equalTo(Ok))
        require(listTopicsResponse is SuccessHttpResponse) { "request was not successful" }
        return listTopicsResponse.result(AdminTopicList::class)
    }

    fun getTopic(topicId: TopicId): AdminTopic {
        val getTopicResponse = httpTemplate.get("/v1/topics/{topicId}")
            .path("topicId", topicId)
            .execute()

        assertThat(getTopicResponse.statusCode, equalTo(Ok))
        require(getTopicResponse is SuccessHttpResponse) { "request was not successful" }
        return getTopicResponse.result(AdminTopic::class)
    }


    fun createTopic(topicName: TopicName): AdminSubmittedReqWithId {
        val createTopicResponse = httpTemplate.post("/v1/groups/{groupId}/topics")
            .path("groupId", testGroup.id)
            .body(AdminCreateTopicReq(topicName))
            .execute()

        assertThat(createTopicResponse.statusCode, equalTo(Accepted))
        require(createTopicResponse is SuccessHttpResponse) { "request was not successful" }

        return createTopicResponse.result(AdminSubmittedReqWithId::class)
    }

    fun appendToTopic(topicId: TopicId, toAppend: TopicEntryPayload): AdminSubmittedReq {
        val createTopicResponse = httpTemplate.post("/v1/topics/{topicId}/entries")
            .path("topicId", topicId)
            .body(toAppend)
            .execute()

        assertThat(createTopicResponse.statusCode, equalTo(Accepted))
        require(createTopicResponse is SuccessHttpResponse) { "request was not successful" }

        return createTopicResponse.result(AdminDefaultSubmittedReq::class)
    }
}