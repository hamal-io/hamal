package io.hamal.backend.instance.web.trigger

import io.hamal.lib.domain.EventTrigger
import io.hamal.lib.domain.FixedRateTrigger
import io.hamal.lib.domain.HamalError
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.req.CreateTriggerReq
import io.hamal.lib.domain.req.SubmittedCreateTriggerReq
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.seconds

internal class CreateTriggerRouteTest : BaseTriggerRouteTest() {

    @Test
    fun `Creates fixed rate trigger`() {
        val funcResponse = awaitCompleted(createFunc(FuncName("fixed-trigger-func")))

        val creationResponse = httpTemplate.post("/v1/triggers")
            .body(
                CreateTriggerReq(
                    type = TriggerType.FixedRate,
                    name = TriggerName("fixed-rate-trigger"),
                    funcId = funcResponse.id,
                    inputs = TriggerInputs(),
                    duration = 10.seconds,
                )
            )
            .execute()

        assertThat(creationResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(creationResponse is SuccessHttpResponse) { "request was not successful" }

        val result = creationResponse.result(SubmittedCreateTriggerReq::class)
        awaitCompleted(result.reqId)

        with(triggerQueryRepository.get(result.id)) {
            assertThat(id, equalTo(result.id))
            assertThat(name, equalTo(TriggerName("fixed-rate-trigger")))
            assertThat(inputs, equalTo(TriggerInputs()))
            assertThat(funcId, equalTo(funcResponse.id))
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

        val result = creationResponse.error(HamalError::class)
        assertThat(result.message, equalTo("Func not found"))
        verifyNoRequests()
    }

    @Test
    fun `Creates event trigger`() {
        val topicResponse = awaitCompleted(createTopic(TopicName("event-trigger-topic")))
        val funcResponse = awaitCompleted(createFunc(FuncName("event-trigger-func")))

        val creationResponse = httpTemplate.post("/v1/triggers")
            .body(
                CreateTriggerReq(
                    type = TriggerType.Event,
                    name = TriggerName("event-trigger"),
                    funcId = funcResponse.id,
                    inputs = TriggerInputs(),
                    topicId = topicResponse.id
                )
            )
            .execute()

        assertThat(creationResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(creationResponse is SuccessHttpResponse) { "request was not successful" }

        val result = creationResponse.result(SubmittedCreateTriggerReq::class)
        awaitCompleted(result.reqId)

        with(triggerQueryRepository.get(result.id)) {
            assertThat(id, equalTo(result.id))
            assertThat(name, equalTo(TriggerName("event-trigger")))
            assertThat(inputs, equalTo(TriggerInputs()))
            assertThat(funcId, equalTo(funcResponse.id))
            require(this is EventTrigger) { "not EventTrigger" }
            assertThat(topicId, equalTo(topicResponse.id))
        }
    }

    @Test
    fun `Tries to create event trigger but does not specify topic id`() {
        val funcResponse = awaitCompleted(createFunc(FuncName("event-trigger-func")))

        val creationResponse = httpTemplate.post("/v1/triggers")
            .body(
                CreateTriggerReq(
                    type = TriggerType.Event,
                    name = TriggerName("event-trigger"),
                    funcId = funcResponse.id,
                    inputs = TriggerInputs()
                )
            )
            .execute()

        assertThat(creationResponse.statusCode, equalTo(HttpStatusCode.BadRequest))
        require(creationResponse is ErrorHttpResponse) { "request was successful" }

        val result = creationResponse.error(HamalError::class)
        assertThat(result.message, equalTo("topicId is missing"))
        verifyNoRequests(SubmittedCreateTriggerReq::class)
    }

    @Test
    fun `Tries to create event trigger but func does not exists`() {
        val funcResponse = awaitCompleted(createFunc(FuncName("event-trigger-func")))

        val creationResponse = httpTemplate.post("/v1/triggers")
            .body(
                CreateTriggerReq(
                    type = TriggerType.Event,
                    name = TriggerName("event-trigger"),
                    funcId = funcResponse.id,
                    inputs = TriggerInputs(),
                    topicId = TopicId(12345)
                )
            )
            .execute()

        assertThat(creationResponse.statusCode, equalTo(HttpStatusCode.NotFound))
        require(creationResponse is ErrorHttpResponse) { "request was successful" }

        val result = creationResponse.error(HamalError::class)
        assertThat(result.message, equalTo("Topic not found"))
        verifyNoRequests(SubmittedCreateTriggerReq::class)
    }

    @Test
    fun `Tries to create event trigger but topic does not exists`() {
        val topicResponse = awaitCompleted(createTopic(TopicName("event-trigger-topic")))

        val creationResponse = httpTemplate.post("/v1/triggers")
            .body(
                CreateTriggerReq(
                    type = TriggerType.Event,
                    name = TriggerName("event-trigger"),
                    funcId = FuncId(1234),
                    inputs = TriggerInputs(),
                    topicId = topicResponse.id
                )
            )
            .execute()

        assertThat(creationResponse.statusCode, equalTo(HttpStatusCode.NotFound))
        require(creationResponse is ErrorHttpResponse) { "request was successful" }

        val result = creationResponse.error(HamalError::class)
        assertThat(result.message, equalTo("Func not found"))
        verifyNoRequests(SubmittedCreateTriggerReq::class)
    }
}
