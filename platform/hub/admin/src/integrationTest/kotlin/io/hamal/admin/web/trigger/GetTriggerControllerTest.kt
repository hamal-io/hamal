package io.hamal.admin.web.trigger

import io.hamal.lib.domain._enum.TriggerType.Event
import io.hamal.lib.domain._enum.TriggerType.FixedRate
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.admin.AdminCreateTriggerReq
import io.hamal.lib.sdk.admin.AdminError
import io.hamal.lib.sdk.admin.AdminEventTrigger
import io.hamal.lib.sdk.admin.AdminFixedRateTrigger
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.seconds

internal class GetTriggerControllerTest : BaseTriggerControllerTest() {
    @Test
    fun `Trigger does not exists`() {
        val getTriggerResponse = httpTemplate.get("/v1/triggers/33333333").execute()
        assertThat(getTriggerResponse.statusCode, equalTo(NotFound))
        require(getTriggerResponse is ErrorHttpResponse) { "request was successful" }

        val error = getTriggerResponse.error(AdminError::class)
        assertThat(error.message, equalTo("Trigger not found"))
    }

    @Test
    fun `Get fixed rate trigger`() {
        val someFuncId = awaitCompleted(createFunc(FuncName("some-func-to-trigger"))).id(::FuncId)
        val triggerId = awaitCompleted(
            createTrigger(
                AdminCreateTriggerReq(
                    type = FixedRate,
                    correlationId = null,
                    name = TriggerName("trigger-one"),
                    inputs = TriggerInputs(MapType(mutableMapOf("hamal" to StringType("rockz")))),
                    funcId = someFuncId,
                    duration = 10.seconds
                )
            )
        ).id(::TriggerId)

        val getTriggerResponse = httpTemplate.get("/v1/triggers/{triggerId}").path("triggerId", triggerId).execute()

        assertThat(getTriggerResponse.statusCode, equalTo(Ok))
        require(getTriggerResponse is SuccessHttpResponse) { "request was not successful" }

        with(getTriggerResponse.result(AdminFixedRateTrigger::class)) {
            assertThat(id, equalTo(triggerId))
            assertThat(name, equalTo(TriggerName("trigger-one")))
            assertThat(inputs, equalTo(TriggerInputs(MapType(mutableMapOf("hamal" to StringType("rockz"))))))
            assertThat(duration, equalTo(10.seconds))
            assertThat(func.id, equalTo(someFuncId))
            assertThat(func.name, equalTo(FuncName("some-func-to-trigger")))
        }
    }

    @Test
    fun `Get event trigger`() {
        val someTopicId = awaitCompleted(createTopic(TopicName("some-topic"))).id(::TopicId)
        val someFuncId = awaitCompleted(createFunc(FuncName("some-func-to-trigger"))).id(::FuncId)

        val triggerId = awaitCompleted(
            createTrigger(
                AdminCreateTriggerReq(
                    type = Event,
                    correlationId = null,
                    name = TriggerName("trigger-one"),
                    inputs = TriggerInputs(MapType(mutableMapOf("hamal" to StringType("rockz")))),
                    funcId = someFuncId,
                    topicId = someTopicId
                )
            )
        ).id(::TriggerId)

        val getTriggerResponse = httpTemplate.get("/v1/triggers/{triggerId}").path("triggerId", triggerId).execute()

        assertThat(getTriggerResponse.statusCode, equalTo(Ok))
        require(getTriggerResponse is SuccessHttpResponse) { "request was not successful" }

        with(getTriggerResponse.result(AdminEventTrigger::class)) {
            assertThat(id, equalTo(triggerId))
            assertThat(name, equalTo(TriggerName("trigger-one")))
            assertThat(inputs, equalTo(TriggerInputs(MapType(mutableMapOf("hamal" to StringType("rockz"))))))
            assertThat(func.id, equalTo(someFuncId))
            assertThat(func.name, equalTo(FuncName("some-func-to-trigger")))
            assertThat(topic.id, equalTo(someTopicId))
            assertThat(topic.name, equalTo(TopicName("some-topic")))
        }
    }
}