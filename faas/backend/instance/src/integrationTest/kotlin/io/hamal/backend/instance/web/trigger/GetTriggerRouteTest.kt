package io.hamal.backend.instance.web.trigger

import io.hamal.lib.domain.EventTrigger
import io.hamal.lib.domain.FixedRateTrigger
import io.hamal.lib.domain.HamalError
import io.hamal.lib.domain._enum.TriggerType.Event
import io.hamal.lib.domain._enum.TriggerType.FixedRate
import io.hamal.lib.domain.req.CreateTriggerReq
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.domain.vo.TriggerInputs
import io.hamal.lib.domain.vo.TriggerName
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.kua.value.StringValue
import io.hamal.lib.kua.value.TableValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.seconds

internal class GetTriggerRouteTest : BaseTriggerRouteTest() {
    @Test
    fun `Trigger does not exists`() {
        val getTriggerResponse = httpTemplate.get("/v1/triggers/33333333").execute()
        assertThat(getTriggerResponse.statusCode, equalTo(HttpStatusCode.NotFound))
        require(getTriggerResponse is ErrorHttpResponse) { "request was successful" }

        val error = getTriggerResponse.error(HamalError::class)
        assertThat(error.message, equalTo("Trigger not found"))
    }

    @Test
    fun `Get fixed rate trigger`() {
        val someFuncId = awaitCompleted(createFunc(FuncName("some-func-to-trigger"))).id
        val trigger = awaitCompleted(
            createTrigger(
                CreateTriggerReq(
                    type = FixedRate,
                    correlationId = null,
                    name = TriggerName("trigger-one"),
                    inputs = TriggerInputs(TableValue("hamal" to StringValue("rockz"))),
                    funcId = someFuncId,
                    duration = 10.seconds
                )
            )
        )

        val getTriggerResponse = httpTemplate.get("/v1/triggers/${trigger.id.value.value}").execute()

        assertThat(getTriggerResponse.statusCode, equalTo(HttpStatusCode.Ok))
        require(getTriggerResponse is SuccessHttpResponse) { "request was not successful" }

        with(getTriggerResponse.result(FixedRateTrigger::class)) {
            assertThat(id, equalTo(trigger.id))
            assertThat(name, equalTo(TriggerName("trigger-one")))
            assertThat(inputs, equalTo(TriggerInputs(TableValue("hamal" to StringValue("rockz")))))
            assertThat(duration, equalTo(10.seconds))
            assertThat(funcId, equalTo(someFuncId))
        }
    }

    @Test
    fun `Get event trigger`() {
        val someTopicId = awaitCompleted(createTopic(TopicName("some-topic"))).id
        val someFuncId = awaitCompleted(createFunc(FuncName("some-func-to-trigger"))).id

        val trigger = awaitCompleted(
            createTrigger(
                CreateTriggerReq(
                    type = Event,
                    correlationId = null,
                    name = TriggerName("trigger-one"),
                    inputs = TriggerInputs(TableValue("hamal" to StringValue("rockz"))),
                    funcId = someFuncId,
                    topicId = someTopicId
                )
            )
        )

        val getTriggerResponse = httpTemplate.get("/v1/triggers/${trigger.id.value.value}").execute()

        assertThat(getTriggerResponse.statusCode, equalTo(HttpStatusCode.Ok))
        require(getTriggerResponse is SuccessHttpResponse) { "request was not successful" }

        with(getTriggerResponse.result(EventTrigger::class)) {
            assertThat(id, equalTo(trigger.id))
            assertThat(name, equalTo(TriggerName("trigger-one")))
            assertThat(inputs, equalTo(TriggerInputs(TableValue("hamal" to StringValue("rockz")))))
            assertThat(funcId, equalTo(someFuncId))
            assertThat(topicId, equalTo(someTopicId))
        }
    }
}