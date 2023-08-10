package io.hamal.backend.instance.web.trigger

import io.hamal.backend.repository.api.EventTrigger
import io.hamal.backend.repository.api.FixedRateTrigger
import io.hamal.backend.repository.api.submitted_req.SubmittedCreateTriggerReq
import io.hamal.lib.sdk.domain.ApiError
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.req.CreateTriggerReq
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.domain.ApiSubmittedReqWithDomainId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.seconds

internal class CreateTriggerRouteTest : BaseTriggerRouteTest() {

    @Test
    fun `Creates fixed rate trigger`() {
        val funcId = awaitCompleted(createFunc(FuncName("fixed-trigger-func"))).id(::FuncId)

        val creationResponse = httpTemplate.post("/v1/triggers")
            .body(
                CreateTriggerReq(
                    type = TriggerType.FixedRate,
                    name = TriggerName("fixed-rate-trigger"),
                    funcId = funcId,
                    inputs = TriggerInputs(),
                    duration = 10.seconds,
                )
            )
            .execute()

        assertThat(creationResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(creationResponse is SuccessHttpResponse) { "request was not successful" }

        val result = creationResponse.result(ApiSubmittedReqWithDomainId::class)
        awaitCompleted(result.reqId)

        with(triggerQueryRepository.get(result.id(::TriggerId))) {
            assertThat(id, equalTo(TriggerId(result.id)))
            assertThat(name, equalTo(TriggerName("fixed-rate-trigger")))
            assertThat(inputs, equalTo(TriggerInputs()))
            assertThat(this.funcId, equalTo(funcId))
            require(this is FixedRateTrigger) { "not FixedRateTrigger" }
            assertThat(duration, equalTo(10.seconds))
        }
    }

    @Test
    fun `Tries to create fixed rate trigger but func does not exist`() {
        val creationResponse = httpTemplate.post("/v1/triggers")
            .body(
                CreateTriggerReq(
                    type = TriggerType.FixedRate,
                    name = TriggerName("fixed-rate-trigger"),
                    funcId = FuncId(123),
                    inputs = TriggerInputs(),
                    duration = 10.seconds,
                )
            )
            .execute()

        assertThat(creationResponse.statusCode, equalTo(HttpStatusCode.NotFound))
        require(creationResponse is ErrorHttpResponse) { "request was successful" }

        val result = creationResponse.error(ApiError::class)
        assertThat(result.message, equalTo("Func not found"))
        verifyNoRequests()
    }

    @Test
    fun `Creates event trigger`() {
        val topicId = awaitCompleted(createTopic(TopicName("event-trigger-topic"))).id(::TopicId)
        val funcId = awaitCompleted(createFunc(FuncName("event-trigger-func"))).id(::FuncId)

        val creationResponse = httpTemplate.post("/v1/triggers")
            .body(
                CreateTriggerReq(
                    type = TriggerType.Event,
                    name = TriggerName("event-trigger"),
                    funcId = funcId,
                    inputs = TriggerInputs(),
                    topicId = topicId
                )
            )
            .execute()

        assertThat(creationResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(creationResponse is SuccessHttpResponse) { "request was not successful" }

        val result = creationResponse.result(ApiSubmittedReqWithDomainId::class)
        awaitCompleted(result.reqId)

        with(triggerQueryRepository.get(result.id(::TriggerId))) {
            assertThat(id, equalTo(TriggerId(result.id)))
            assertThat(name, equalTo(TriggerName("event-trigger")))
            assertThat(inputs, equalTo(TriggerInputs()))
            assertThat(this.funcId, equalTo(funcId))
            require(this is EventTrigger) { "not EventTrigger" }
            assertThat(this.topicId, equalTo(topicId))
        }
    }

    @Test
    fun `Tries to create event trigger but does not specify topic id`() {
        val funcId = awaitCompleted(createFunc(FuncName("event-trigger-func"))).id(::FuncId)

        val creationResponse = httpTemplate.post("/v1/triggers")
            .body(
                CreateTriggerReq(
                    type = TriggerType.Event,
                    name = TriggerName("event-trigger"),
                    funcId = funcId,
                    inputs = TriggerInputs()
                )
            )
            .execute()

        assertThat(creationResponse.statusCode, equalTo(HttpStatusCode.BadRequest))
        require(creationResponse is ErrorHttpResponse) { "request was successful" }

        val result = creationResponse.error(ApiError::class)
        assertThat(result.message, equalTo("topicId is missing"))
        verifyNoRequests(SubmittedCreateTriggerReq::class)
    }

    @Test
    fun `Tries to create event trigger but func does not exists`() {
        val funcId = awaitCompleted(createFunc(FuncName("event-trigger-func"))).id(::FuncId)

        val creationResponse = httpTemplate.post("/v1/triggers")
            .body(
                CreateTriggerReq(
                    type = TriggerType.Event,
                    name = TriggerName("event-trigger"),
                    funcId = funcId,
                    inputs = TriggerInputs(),
                    topicId = TopicId(12345)
                )
            )
            .execute()

        assertThat(creationResponse.statusCode, equalTo(HttpStatusCode.NotFound))
        require(creationResponse is ErrorHttpResponse) { "request was successful" }

        val result = creationResponse.error(ApiError::class)
        assertThat(result.message, equalTo("Topic not found"))
        verifyNoRequests(SubmittedCreateTriggerReq::class)
    }

    @Test
    fun `Tries to create event trigger but topic does not exists`() {
        val topicId = awaitCompleted(createTopic(TopicName("event-trigger-topic"))).id(::TopicId)

        val creationResponse = httpTemplate.post("/v1/triggers")
            .body(
                CreateTriggerReq(
                    type = TriggerType.Event,
                    name = TriggerName("event-trigger"),
                    funcId = FuncId(1234),
                    inputs = TriggerInputs(),
                    topicId = topicId
                )
            )
            .execute()

        assertThat(creationResponse.statusCode, equalTo(HttpStatusCode.NotFound))
        require(creationResponse is ErrorHttpResponse) { "request was successful" }

        val result = creationResponse.error(ApiError::class)
        assertThat(result.message, equalTo("Func not found"))
        verifyNoRequests(SubmittedCreateTriggerReq::class)
    }
}
