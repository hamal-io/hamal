package io.hamal.api.http.controller.trigger

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain._enum.HookMethod
import io.hamal.lib.domain._enum.HookMethod.Get
import io.hamal.lib.domain._enum.HookMethod.Post
import io.hamal.lib.domain._enum.TriggerStatus
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain._enum.TriggerType.Cron
import io.hamal.lib.domain._enum.TriggerType.FixedRate
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode.*
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiTriggerCreateReq
import io.hamal.lib.sdk.api.ApiTriggerCreateSubmitted
import io.hamal.repository.api.*
import io.hamal.repository.api.submitted_req.TriggerCreateSubmitted
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.seconds

internal class TriggerCreateControllerTest : TriggerBaseControllerTest() {

    @Test
    fun `Creates trigger without flow id`() {
        val funcId = awaitCompleted(createFunc(FuncName("fixed-trigger-func"))).funcId

        val creationResponse = httpTemplate.post("/v1/flows/1/triggers").body(
            ApiTriggerCreateReq(
                type = FixedRate,
                name = TriggerName("trigger"),
                funcId = funcId,
                inputs = TriggerInputs(),
                duration = 10.seconds,
            )
        ).execute()

        assertThat(creationResponse.statusCode, equalTo(Accepted))
        require(creationResponse is HttpSuccessResponse) { "request was not successful" }

        val result = creationResponse.result(ApiTriggerCreateSubmitted::class)
        awaitCompleted(result)

        with(getTrigger(result.triggerId)) {
            assertThat(id, equalTo(result.triggerId))
            assertThat(name, equalTo(TriggerName("trigger")))
            assertThat(flow.name, equalTo(FlowName("hamal")))
            assertThat(status, equalTo(TriggerStatus.Active))
        }
    }


    @Test
    fun `Creates trigger with flow id`() {
        val flow = flowCmdRepository.create(
            FlowCmdRepository.CreateCmd(
                id = CmdId(1),
                flowId = FlowId(2345),
                groupId = testGroup.id,
                name = FlowName("hamal::flow"),
                inputs = FlowInputs()
            )
        )

        val funcId = awaitCompleted(createFunc(FuncName("fixed-trigger-func"))).funcId

        val creationResponse =
            httpTemplate.post("/v1/flows/{flowId}/triggers").path("flowId", flow.id).body(
                ApiTriggerCreateReq(
                    type = FixedRate,
                    name = TriggerName("trigger"),
                    funcId = funcId,
                    inputs = TriggerInputs(),
                    duration = 10.seconds,
                )
            ).execute()

        assertThat(creationResponse.statusCode, equalTo(Accepted))
        require(creationResponse is HttpSuccessResponse) { "request was not successful" }

        val result = creationResponse.result(ApiTriggerCreateSubmitted::class)
        awaitCompleted(result)

        with(getTrigger(result.triggerId)) {
            assertThat(id, equalTo(result.triggerId))
            assertThat(status, equalTo(TriggerStatus.Active))
            assertThat(name, equalTo(TriggerName("trigger")))
            assertThat(flow.id, equalTo(flow.id))
            assertThat(flow.name, equalTo(FlowName("hamal::flow")))
        }
    }

    @Test
    fun `Tries to create trigger with flow id but flow does not exist`() {
        val funcId = awaitCompleted(createFunc(FuncName("fixed-trigger-func"))).funcId

        val creationResponse = httpTemplate.post("/v1/flows/12345/triggers").body(
            ApiTriggerCreateReq(
                type = FixedRate,
                name = TriggerName("trigger"),
                funcId = funcId,
                inputs = TriggerInputs(),
                duration = 10.seconds,
            )
        ).execute()

        assertThat(creationResponse.statusCode, equalTo(NotFound))
        require(creationResponse is HttpErrorResponse) { "request was successful" }

        val error = creationResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Flow not found"))

        assertThat(listTriggers().triggers, empty())
    }

    @Nested
    inner class FixedRateTest {

        @Test
        fun `Creates trigger`() {
            val funcId = awaitCompleted(createFunc(FuncName("fixed-trigger-func"))).funcId

            val creationResponse = httpTemplate.post("/v1/flows/1/triggers").body(
                ApiTriggerCreateReq(
                    type = FixedRate,
                    name = TriggerName("fixed-rate-trigger"),
                    funcId = funcId,
                    inputs = TriggerInputs(),
                    duration = 10.seconds,
                )
            ).execute()

            assertThat(creationResponse.statusCode, equalTo(Accepted))
            require(creationResponse is HttpSuccessResponse) { "request was not successful" }

            val result = creationResponse.result(ApiTriggerCreateSubmitted::class)
            awaitCompleted(result)

            with(triggerQueryRepository.get(result.triggerId)) {
                assertThat(id, equalTo(result.triggerId))
                assertThat(name, equalTo(TriggerName("fixed-rate-trigger")))
                assertThat(inputs, equalTo(TriggerInputs()))
                assertThat(this.funcId, equalTo(funcId))
                require(this is FixedRateTrigger) { "not FixedRateTrigger" }
                assertThat(duration, equalTo(10.seconds))
            }
        }

        @Test
        fun `Tries to create trigger but func does not exist`() {
            val creationResponse = httpTemplate.post("/v1/flows/1/triggers").body(
                ApiTriggerCreateReq(
                    type = FixedRate,
                    name = TriggerName("fixed-rate-trigger"),
                    funcId = FuncId(123),
                    inputs = TriggerInputs(),
                    duration = 10.seconds,
                )
            ).execute()

            assertThat(creationResponse.statusCode, equalTo(NotFound))
            require(creationResponse is HttpErrorResponse) { "request was successful" }

            val result = creationResponse.error(ApiError::class)
            assertThat(result.message, equalTo("Func not found"))
            verifyNoRequests()
        }
    }

    @Nested
    inner class EventTriggerTest {

        @Test
        fun `Creates trigger`() {
            val topicId = awaitCompleted(createTopic(TopicName("event-trigger-topic"))).topicId
            val funcId = awaitCompleted(createFunc(FuncName("event-trigger-func"))).funcId

            val creationResponse = httpTemplate.post("/v1/flows/1/triggers").body(
                ApiTriggerCreateReq(
                    type = TriggerType.Event,
                    name = TriggerName("event-trigger"),
                    funcId = funcId,
                    inputs = TriggerInputs(),
                    topicId = topicId
                )
            ).execute()

            assertThat(creationResponse.statusCode, equalTo(Accepted))
            require(creationResponse is HttpSuccessResponse) { "request was not successful" }

            val result = creationResponse.result(ApiTriggerCreateSubmitted::class)
            awaitCompleted(result)

            with(triggerQueryRepository.get(result.triggerId)) {
                assertThat(id, equalTo(result.triggerId))
                assertThat(name, equalTo(TriggerName("event-trigger")))
                assertThat(inputs, equalTo(TriggerInputs()))
                assertThat(this.funcId, equalTo(funcId))
                require(this is EventTrigger) { "not EventTrigger" }
                assertThat(this.topicId, equalTo(topicId))
            }
        }

        @Test
        fun `Tries to create trigger but does not specify topic id`() {
            val funcId = awaitCompleted(createFunc(FuncName("event-trigger-func"))).funcId

            val creationResponse = httpTemplate.post("/v1/flows/1/triggers").body(
                ApiTriggerCreateReq(
                    type = TriggerType.Event,
                    name = TriggerName("event-trigger"),
                    funcId = funcId,
                    inputs = TriggerInputs(),
                    topicId = null
                )
            ).execute()

            assertThat(creationResponse.statusCode, equalTo(BadRequest))
            require(creationResponse is HttpErrorResponse) { "request was successful" }

            val result = creationResponse.error(ApiError::class)
            assertThat(result.message, equalTo("topicId is missing"))
            verifyNoRequests(TriggerCreateSubmitted::class)
        }

        @Test
        fun `Tries to create trigger but topic does not exist`() {
            val funcId = awaitCompleted(createFunc(FuncName("event-trigger-func"))).funcId

            val creationResponse = httpTemplate.post("/v1/flows/1/triggers").body(
                ApiTriggerCreateReq(
                    type = TriggerType.Event,
                    name = TriggerName("event-trigger"),
                    funcId = funcId,
                    inputs = TriggerInputs(),
                    topicId = TopicId(1234)
                )
            ).execute()

            assertThat(creationResponse.statusCode, equalTo(NotFound))
            require(creationResponse is HttpErrorResponse) { "request was successful" }

            val result = creationResponse.error(ApiError::class)
            assertThat(result.message, equalTo("Topic not found"))
            verifyNoRequests(TriggerCreateSubmitted::class)
        }

        @Test
        fun `Tries to create trigger but func does not exists`() {
            val topicId = awaitCompleted(createTopic(TopicName("event-trigger-topic"))).topicId

            val creationResponse = httpTemplate.post("/v1/flows/1/triggers").body(
                ApiTriggerCreateReq(
                    type = TriggerType.Event,
                    name = TriggerName("event-trigger"),
                    funcId = FuncId(1234),
                    inputs = TriggerInputs(),
                    topicId = topicId
                )
            ).execute()

            assertThat(creationResponse.statusCode, equalTo(NotFound))
            require(creationResponse is HttpErrorResponse) { "request was successful" }

            val result = creationResponse.error(ApiError::class)
            assertThat(result.message, equalTo("Func not found"))
            verifyNoRequests(TriggerCreateSubmitted::class)
        }
    }

    @Nested
    inner class HookTriggerTest {
        @Test
        fun `Creates trigger`() {
            val hookId = awaitCompleted(createHook(HookName("hook-name"))).hookId
            val funcId = awaitCompleted(createFunc(FuncName("hook-trigger-func"))).funcId

            val creationResponse = httpTemplate.post("/v1/flows/1/triggers").body(
                ApiTriggerCreateReq(
                    type = TriggerType.Hook,
                    name = TriggerName("hook-trigger"),
                    funcId = funcId,
                    inputs = TriggerInputs(),
                    hookId = hookId,
                    hookMethod = Post
                )
            ).execute()

            assertThat(creationResponse.statusCode, equalTo(Accepted))
            require(creationResponse is HttpSuccessResponse) { "request was not successful" }

            val result = creationResponse.result(ApiTriggerCreateSubmitted::class)
            awaitCompleted(result)

            with(triggerQueryRepository.get(result.triggerId)) {
                assertThat(id, equalTo(result.triggerId))
                assertThat(name, equalTo(TriggerName("hook-trigger")))
                assertThat(inputs, equalTo(TriggerInputs()))
                assertThat(this.funcId, equalTo(funcId))
                require(this is HookTrigger) { "not HookTrigger" }
                assertThat(this.hookId, equalTo(hookId))
                assertThat(this.hookMethod, equalTo(Post))
            }
        }


        @Test
        fun `Creates multiple triggers`() {
            val hook_one = awaitCompleted(createHook(HookName("hook-name-one"))).hookId
            val hook_two = awaitCompleted(createHook(HookName("hook-name-two"))).hookId
            val func_one = awaitCompleted(createFunc(FuncName("hook-trigger-func-one"))).funcId
            val func_two = awaitCompleted(createFunc(FuncName("hook-trigger-func-two"))).funcId


            awaitCompleted(
                createHookTrigger(
                    name = TriggerName("trigger1"),
                    funcId = func_one,
                    hookId = hook_one,
                    hookMethod = Get
                )
            )
            awaitCompleted(
                createHookTrigger(
                    name = TriggerName("trigger2"),
                    funcId = func_one,
                    hookId = hook_one,
                    hookMethod = Post
                )
            )
            awaitCompleted(
                createHookTrigger(
                    name = TriggerName("trigger3"),
                    funcId = func_two,
                    hookId = hook_one,
                    hookMethod = Get
                )
            )
            awaitCompleted(
                createHookTrigger(
                    name = TriggerName("trigger4"),
                    funcId = func_one,
                    hookId = hook_two,
                    hookMethod = Get
                )
            )


            assertThat(
                triggerQueryRepository.list(
                    TriggerQueryRepository.TriggerQuery(
                        groupIds = listOf(testGroup.id),
                        limit = Limit(10)

                    )
                ), hasSize(4)
            )
        }

        @Test
        fun `Tries to create trigger but does not specify hook id`() {
            val funcId = awaitCompleted(createFunc(FuncName("hook-trigger-func"))).funcId

            val creationResponse = httpTemplate.post("/v1/flows/1/triggers").body(
                ApiTriggerCreateReq(
                    type = TriggerType.Hook,
                    name = TriggerName("hook-trigger"),
                    funcId = funcId,
                    inputs = TriggerInputs(),
                    hookId = null
                )
            ).execute()

            assertThat(creationResponse.statusCode, equalTo(BadRequest))
            require(creationResponse is HttpErrorResponse) { "request was successful" }

            val result = creationResponse.error(ApiError::class)
            assertThat(result.message, equalTo("hookId is missing"))
            verifyNoRequests(TriggerCreateSubmitted::class)
        }


        @Test
        fun `Tries to create a trigger when hookId, funcId, hookMethod already exist`() {
            val hook_one = awaitCompleted(createHook(HookName("hook-name-one"))).hookId
            val func_one = awaitCompleted(createFunc(FuncName("hook-trigger-func-one"))).funcId

            httpTemplate.post("/v1/flows/1/triggers").body(
                ApiTriggerCreateReq(
                    type = TriggerType.Hook,
                    name = TriggerName("hook-trigger-1"),
                    funcId = func_one,
                    inputs = TriggerInputs(),
                    hookId = hook_one,
                    hookMethod = Get
                )
            ).execute()

            val creationResponse = httpTemplate.post("/v1/flows/1/triggers").body(
                ApiTriggerCreateReq(
                    type = TriggerType.Hook,
                    name = TriggerName("hook-trigger-2"),
                    funcId = func_one,
                    inputs = TriggerInputs(),
                    hookId = hook_one,
                    hookMethod = Get
                )
            ).execute()

            assertThat(creationResponse.statusCode, equalTo(BadRequest))
            require(creationResponse is HttpErrorResponse) { "request was successful" }

            val result = creationResponse.error(ApiError::class)
            assertThat(result.message, equalTo("Trigger already exists"))

            assertThat(
                triggerQueryRepository.list(
                    TriggerQueryRepository.TriggerQuery(
                        groupIds = listOf(testGroup.id),
                        limit = Limit(10)

                    )
                ), hasSize(1)
            )
        }

        @Test
        fun `Tries to create trigger but does not specify hook method`() {
            val hookId = awaitCompleted(createHook(HookName("hook-name"))).hookId
            val funcId = awaitCompleted(createFunc(FuncName("hook-trigger-func"))).funcId

            val creationResponse = httpTemplate.post("/v1/flows/1/triggers").body(
                ApiTriggerCreateReq(
                    type = TriggerType.Hook,
                    name = TriggerName("hook-trigger"),
                    funcId = funcId,
                    inputs = TriggerInputs(),
                    hookId = hookId,
                    hookMethod = null
                )
            ).execute()

            assertThat(creationResponse.statusCode, equalTo(BadRequest))
            require(creationResponse is HttpErrorResponse) { "request was successful" }

            val result = creationResponse.error(ApiError::class)
            assertThat(result.message, equalTo("hookMethod is missing"))
            verifyNoRequests(TriggerCreateSubmitted::class)
        }

        @Test
        fun `Tries to create trigger but hook does not exist`() {
            val funcId = awaitCompleted(createFunc(FuncName("hook-trigger-func"))).funcId

            val creationResponse = httpTemplate.post("/v1/flows/1/triggers").body(
                ApiTriggerCreateReq(
                    type = TriggerType.Hook,
                    name = TriggerName("hook-trigger"),
                    funcId = funcId,
                    inputs = TriggerInputs(),
                    hookId = HookId(1234),
                    hookMethod = Get
                )
            ).execute()

            assertThat(creationResponse.statusCode, equalTo(NotFound))
            require(creationResponse is HttpErrorResponse) { "request was successful" }

            val result = creationResponse.error(ApiError::class)
            assertThat(result.message, equalTo("Hook not found"))
            verifyNoRequests(TriggerCreateSubmitted::class)
        }

        @Test
        fun `Tries to create trigger but topic does not exists`() {
            val hookId = awaitCompleted(createHook(HookName("hook-name"))).hookId

            val creationResponse = httpTemplate.post("/v1/flows/1/triggers").body(
                ApiTriggerCreateReq(
                    type = TriggerType.Hook,
                    name = TriggerName("hook-trigger"),
                    funcId = FuncId(1234),
                    inputs = TriggerInputs(),
                    hookId = hookId
                )
            ).execute()

            assertThat(creationResponse.statusCode, equalTo(NotFound))
            require(creationResponse is HttpErrorResponse) { "request was successful" }

            val result = creationResponse.error(ApiError::class)
            assertThat(result.message, equalTo("Func not found"))
            verifyNoRequests(TriggerCreateSubmitted::class)
        }
    }

    @Nested
    inner class CronTriggerTest {
        @Test
        fun `Creates Trigger`() {
            val funcId = awaitCompleted(createFunc(FuncName("cron-trigger-func"))).funcId

            val creationResponse = httpTemplate.post("/v1/flows/1/triggers").body(
                ApiTriggerCreateReq(
                    type = Cron,
                    name = TriggerName("cron-trigger"),
                    funcId = funcId,
                    inputs = TriggerInputs(),
                    cron = CronPattern("0 0 9-17 * * MON-FRI")
                )
            ).execute()

            assertThat(creationResponse.statusCode, equalTo(Accepted))
            require(creationResponse is HttpSuccessResponse) { "request was not successful" }

            val result = creationResponse.result(ApiTriggerCreateSubmitted::class)
            awaitCompleted(result)

            with(triggerQueryRepository.get(result.triggerId)) {
                assertThat(id, equalTo(result.triggerId))
                assertThat(name, equalTo(TriggerName("cron-trigger")))
                assertThat(inputs, equalTo(TriggerInputs()))
                assertThat(this.funcId, equalTo(funcId))
                require(this is CronTrigger) { "not cron trigger" }
                assertThat(cron, equalTo(CronPattern("0 0 9-17 * * MON-FRI")))
            }
        }

        @Test
        fun `Tries to create trigger but cron expression is invalid`() {
            val funcId = awaitCompleted(createFunc(FuncName("cron-trigger-func"))).funcId
            val response = httpTemplate.post("/v1/flows/1/triggers").body(
                """{
                |"type":"Cron",
                |"name": "cron-trigger",
                |"funcId": "${funcId.value.value.toString(16)}",
                |"inputs": {},
                |"cron": "Invalid cron pattern"
                |}""".trimMargin()
            ).execute()

            require(response is HttpErrorResponse)
            assertThat(response.statusCode, equalTo(BadRequest))
            assertThat(response.error(ApiError::class), equalTo(ApiError("Invalid Cron Expression")))
        }
    }

    private fun createHookTrigger(
        name: TriggerName,
        funcId: FuncId,
        hookId: HookId,
        hookMethod: HookMethod
    ): ApiTriggerCreateSubmitted {
        val creationResponse = httpTemplate.post("/v1/flows/1/triggers").body(
            ApiTriggerCreateReq(
                type = TriggerType.Hook,
                name = name,
                funcId = funcId,
                inputs = TriggerInputs(),
                hookId = hookId,
                hookMethod = hookMethod
            )
        ).execute()

        assertThat(creationResponse.statusCode, equalTo(Accepted))
        require(creationResponse is HttpSuccessResponse) { "request was not successful" }
        return creationResponse.result(ApiTriggerCreateSubmitted::class)
    }
}

