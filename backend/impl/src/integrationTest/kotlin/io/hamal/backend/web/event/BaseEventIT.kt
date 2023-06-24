package io.hamal.backend.web.event

import io.hamal.backend.repository.api.ReqQueryRepository
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.backend.service.query.EventQueryService
import io.hamal.backend.web.BaseIT
import io.hamal.lib.domain.req.CreateTopicReq
import io.hamal.lib.domain.req.SubmittedCreateTopicReq
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.domain.ListTopicsResponse
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class BaseEventIT(
    localPort: Int,
    reqQueryRepository: ReqQueryRepository,
    eventBrokerRepository: LogBrokerRepository<*>,
    val eventQueryService: EventQueryService<*>
) : BaseIT(
    localPort = localPort,
    reqQueryRepository = reqQueryRepository,
    eventBrokerRepository = eventBrokerRepository
) {

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

}