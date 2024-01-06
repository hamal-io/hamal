package io.hamal.api.http.controller.trigger

import io.hamal.lib.domain._enum.HookMethod.*
import io.hamal.lib.domain._enum.TriggerType.*
import io.hamal.lib.domain._enum.TriggerType.Event
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.kua.type.KuaMap
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.sdk.api.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.seconds

internal class TriggerGetControllerTest : TriggerBaseControllerTest() {
    @Test
    fun `Trigger does not exists`() {
        val getTriggerResponse = httpTemplate.get("/v1/triggers/33333333").execute()
        assertThat(getTriggerResponse.statusCode, equalTo(NotFound))
        require(getTriggerResponse is HttpErrorResponse) { "request was successful" }

        val error = getTriggerResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Trigger not found"))
    }

    @Test
    fun `Get fixed rate trigger`() {
        val someFuncId = awaitCompleted(createFunc(FuncName("some-func-to-trigger"))).funcId
        val triggerId = awaitCompleted(
            createTrigger(
                ApiTriggerCreateReq(
                    type = FixedRate,
                    correlationId = null,
                    name = TriggerName("trigger-one"),
                    inputs = TriggerInputs(KuaMap(mutableMapOf("hamal" to KuaString("rockz")))),
                    funcId = someFuncId,
                    duration = 10.seconds
                )
            )
        ).triggerId

        val getTriggerResponse = httpTemplate.get("/v1/triggers/{triggerId}").path("triggerId", triggerId).execute()

        assertThat(getTriggerResponse.statusCode, equalTo(Ok))
        require(getTriggerResponse is HttpSuccessResponse) { "request was not successful" }

        with(getTriggerResponse.result(ApiFixedRateTrigger::class)) {
            assertThat(id, equalTo(triggerId))
            assertThat(name, equalTo(TriggerName("trigger-one")))
            assertThat(inputs, equalTo(TriggerInputs(KuaMap(mutableMapOf("hamal" to KuaString("rockz"))))))
            assertThat(duration, equalTo(10.seconds))
            assertThat(func.id, equalTo(someFuncId))
            assertThat(func.name, equalTo(FuncName("some-func-to-trigger")))
        }
    }

    @Test
    fun `Get event trigger`() {
        val someTopicId = awaitCompleted(createTopic(TopicName("some-topic"))).topicId
        val someFuncId = awaitCompleted(createFunc(FuncName("some-func-to-trigger"))).funcId

        val triggerId = awaitCompleted(
            createTrigger(
                ApiTriggerCreateReq(
                    type = Event,
                    correlationId = null,
                    name = TriggerName("trigger-one"),
                    inputs = TriggerInputs(KuaMap(mutableMapOf("hamal" to KuaString("rockz")))),
                    funcId = someFuncId,
                    topicId = someTopicId
                )
            )
        ).triggerId

        val getTriggerResponse = httpTemplate.get("/v1/triggers/{triggerId}").path("triggerId", triggerId).execute()

        assertThat(getTriggerResponse.statusCode, equalTo(Ok))
        require(getTriggerResponse is HttpSuccessResponse) { "request was not successful" }

        with(getTriggerResponse.result(ApiEventTrigger::class)) {
            assertThat(id, equalTo(triggerId))
            assertThat(name, equalTo(TriggerName("trigger-one")))
            assertThat(inputs, equalTo(TriggerInputs(KuaMap(mutableMapOf("hamal" to KuaString("rockz"))))))
            assertThat(func.id, equalTo(someFuncId))
            assertThat(func.name, equalTo(FuncName("some-func-to-trigger")))
            assertThat(topic.id, equalTo(someTopicId))
            assertThat(topic.name, equalTo(TopicName("some-topic")))
        }
    }

    @Test
    fun `Get hook trigger`() {
        val hookId = awaitCompleted(createHook(HookName("some-hook"))).hookId
        val funcId = awaitCompleted(createFunc(FuncName("some-func-to-trigger"))).funcId

        val triggerId = awaitCompleted(
            createTrigger(
                ApiTriggerCreateReq(
                    type = Hook,
                    correlationId = null,
                    name = TriggerName("trigger-one"),
                    inputs = TriggerInputs(KuaMap(mutableMapOf("hamal" to KuaString("rockz")))),
                    funcId = funcId,
                    hookId = hookId,
                    hookMethod = Get

                )
            )
        ).triggerId

        val getTriggerResponse = httpTemplate.get("/v1/triggers/{triggerId}").path("triggerId", triggerId).execute()

        assertThat(getTriggerResponse.statusCode, equalTo(Ok))
        require(getTriggerResponse is HttpSuccessResponse) { "request was not successful" }

        with(getTriggerResponse.result(ApiHookTrigger::class)) {
            assertThat(id, equalTo(triggerId))
            assertThat(name, equalTo(TriggerName("trigger-one")))
            assertThat(inputs, equalTo(TriggerInputs(KuaMap(mutableMapOf("hamal" to KuaString("rockz"))))))
            assertThat(func.id, equalTo(funcId))
            assertThat(func.name, equalTo(FuncName("some-func-to-trigger")))
            assertThat(hook.id, equalTo(hookId))
            assertThat(hook.name, equalTo(HookName("some-hook")))
            assertThat(hook.method, equalTo(Get))
        }
    }

    @Test
    fun `Get cron trigger`() {
        val funcId = awaitCompleted(createFunc(FuncName("some-func-to-trigger"))).funcId

        val triggerId = awaitCompleted(
            createTrigger(
                ApiTriggerCreateReq(
                    type = Cron,
                    name = TriggerName("cron-trigger"),
                    funcId = funcId,
                    inputs = TriggerInputs(KuaMap(mutableMapOf("hamal" to KuaString("rockz")))),
                    cron = CronPattern("0 0 9-17 * * MON-FRI")
                )
            )
        ).triggerId

        val getTriggerResponse = httpTemplate.get("/v1/triggers/{triggerId}").path("triggerId", triggerId).execute()

        assertThat(getTriggerResponse.statusCode, equalTo(Ok))
        require(getTriggerResponse is HttpSuccessResponse) { "request was not successful" }

        with(getTriggerResponse.result(ApiCronTrigger::class)) {
            assertThat(id, equalTo(triggerId))
            assertThat(name, equalTo(TriggerName("cron-trigger")))
            assertThat(inputs, equalTo(TriggerInputs(KuaMap(mutableMapOf("hamal" to KuaString("rockz"))))))
            assertThat(func.id, equalTo(funcId))
            assertThat(func.name, equalTo(FuncName("some-func-to-trigger")))
            assertThat(cron, equalTo(CronPattern("0 0 9-17 * * MON-FRI")))
        }


    }
}