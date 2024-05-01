package io.hamal.api.http.controller.trigger

import io.hamal.lib.common.domain.CmdId.Companion.CmdId
import io.hamal.lib.common.domain.Limit.Companion.Limit
import io.hamal.lib.domain._enum.TriggerStatus
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain._enum.TriggerType.Cron
import io.hamal.lib.domain._enum.TriggerType.FixedRate
import io.hamal.lib.domain.request.TriggerCreateRequested
import io.hamal.lib.domain.vo.CronPattern.Companion.CronPattern
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.FuncId.Companion.FuncId
import io.hamal.lib.domain.vo.FuncName.Companion.FuncName
import io.hamal.lib.domain.vo.NamespaceFeatures
import io.hamal.lib.domain.vo.NamespaceId.Companion.NamespaceId
import io.hamal.lib.domain.vo.NamespaceName.Companion.NamespaceName
import io.hamal.lib.domain.vo.TopicId.Companion.TopicId
import io.hamal.lib.domain.vo.TopicName.Companion.TopicName
import io.hamal.lib.domain.vo.TriggerDuration.Companion.TriggerDuration
import io.hamal.lib.domain.vo.TriggerInputs
import io.hamal.lib.domain.vo.TriggerName
import io.hamal.lib.domain.vo.TriggerName.Companion.TriggerName
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode.*
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiTriggerCreateReq
import io.hamal.lib.sdk.api.ApiTriggerCreateRequested
import io.hamal.repository.api.NamespaceCmdRepository
import io.hamal.repository.api.Trigger
import io.hamal.repository.api.TriggerQueryRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.seconds

internal class TriggerCreateControllerTest : TriggerBaseControllerTest() {

    @Test
    fun `Creates trigger without namespace id`() {
        val funcId = awaitCompleted(createFunc(FuncName("fixed-trigger-func"))).id

        val creationResponse = httpTemplate.post("/v1/namespaces/539/triggers").body(
            ApiTriggerCreateReq(
                type = FixedRate,
                name = TriggerName("trigger"),
                funcId = funcId,
                inputs = TriggerInputs(),
                duration = TriggerDuration(10.seconds.toIsoString()),
            )
        ).execute()

        assertThat(creationResponse.statusCode, equalTo(Accepted))
        require(creationResponse is HttpSuccessResponse) { "request was not successful" }

        val result = creationResponse.result(ApiTriggerCreateRequested::class)
        awaitCompleted(result)

        with(getTrigger(result.id)) {
            assertThat(id, equalTo(result.id))
            assertThat(name, equalTo(TriggerName("trigger")))
            assertThat(namespace.name, equalTo(NamespaceName("hamal")))
            assertThat(status, equalTo(TriggerStatus.Active))
        }
    }


    @Test
    fun `Creates trigger with namespace id`() {
        val namespace = namespaceCmdRepository.create(
            NamespaceCmdRepository.CreateCmd(
                id = CmdId(1),
                namespaceId = NamespaceId(2345),
                workspaceId = testWorkspace.id,
                name = NamespaceName("hamal::namespace"),
                features = NamespaceFeatures.default
            )
        )

        val funcId = awaitCompleted(createFunc(FuncName("fixed-trigger-func"))).id

        val creationResponse =
            httpTemplate.post("/v1/namespaces/{namespaceId}/triggers").path("namespaceId", namespace.id).body(
                ApiTriggerCreateReq(
                    type = FixedRate,
                    name = TriggerName("trigger"),
                    funcId = funcId,
                    inputs = TriggerInputs(),
                    duration = TriggerDuration(10.seconds.toIsoString()),
                )
            ).execute()

        assertThat(creationResponse.statusCode, equalTo(Accepted))
        require(creationResponse is HttpSuccessResponse) { "request was not successful" }

        val result = creationResponse.result(ApiTriggerCreateRequested::class)
        awaitCompleted(result)

        with(getTrigger(result.id)) {
            assertThat(id, equalTo(result.id))
            assertThat(status, equalTo(TriggerStatus.Active))
            assertThat(name, equalTo(TriggerName("trigger")))
            assertThat(namespace.id, equalTo(namespace.id))
            assertThat(namespace.name, equalTo(NamespaceName("hamal::namespace")))
        }
    }

    @Test
    fun `Tries to create trigger with namespace id but namespace does not exist`() {
        val funcId = awaitCompleted(createFunc(FuncName("fixed-trigger-func"))).id

        val creationResponse = httpTemplate.post("/v1/namespaces/5392345/triggers").body(
            ApiTriggerCreateReq(
                type = FixedRate,
                name = TriggerName("trigger"),
                funcId = funcId,
                inputs = TriggerInputs(),
                duration = TriggerDuration(10.seconds.toIsoString()),
            )
        ).execute()

        assertThat(creationResponse.statusCode, equalTo(NotFound))
        require(creationResponse is HttpErrorResponse) { "request was successful" }

        val error = creationResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Namespace not found"))

        assertThat(listTriggers().triggers, empty())
    }

    @Nested
    inner class FixedRateTest {

        @Test
        fun `Creates trigger`() {
            val funcId = awaitCompleted(createFunc(FuncName("fixed-trigger-func"))).id

            val creationResponse = httpTemplate.post("/v1/namespaces/539/triggers").body(
                ApiTriggerCreateReq(
                    type = FixedRate,
                    name = TriggerName("fixed-rate-trigger"),
                    funcId = funcId,
                    inputs = TriggerInputs(),
                    duration = TriggerDuration("PT10S"),
                )
            ).execute()

            assertThat(creationResponse.statusCode, equalTo(Accepted))
            require(creationResponse is HttpSuccessResponse) { "request was not successful" }

            val result = creationResponse.result(ApiTriggerCreateRequested::class)
            awaitCompleted(result)

            with(triggerQueryRepository.get(result.id)) {
                assertThat(id, equalTo(result.id))
                assertThat(name, equalTo(TriggerName("fixed-rate-trigger")))
                assertThat(inputs, equalTo(TriggerInputs()))
                assertThat(this.funcId, equalTo(funcId))
                require(this is Trigger.FixedRate) { "not FixedRateTrigger" }
                assertThat(duration, equalTo(TriggerDuration("PT10S")))
            }
        }

        @Test
        fun `Tries to create trigger but func does not exist`() {
            val creationResponse = httpTemplate.post("/v1/namespaces/539/triggers").body(
                ApiTriggerCreateReq(
                    type = FixedRate,
                    name = TriggerName("fixed-rate-trigger"),
                    funcId = FuncId(123),
                    inputs = TriggerInputs(),
                    duration = TriggerDuration(10.seconds.toIsoString()),
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
            val topicId = awaitCompleted(createTopic(TopicName("event-trigger-topic"))).id
            val funcId = awaitCompleted(createFunc(FuncName("event-trigger-func"))).id

            val creationResponse = httpTemplate.post("/v1/namespaces/539/triggers").body(
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

            val result = creationResponse.result(ApiTriggerCreateRequested::class)
            awaitCompleted(result)

            with(triggerQueryRepository.get(result.id)) {
                assertThat(id, equalTo(result.id))
                assertThat(name, equalTo(TriggerName("event-trigger")))
                assertThat(inputs, equalTo(TriggerInputs()))
                assertThat(this.funcId, equalTo(funcId))
                require(this is Trigger.Event) { "not EventTrigger" }
                assertThat(this.topicId, equalTo(topicId))
            }
        }

        @Test
        fun `Tries to create trigger but does not specify topic id`() {
            val funcId = awaitCompleted(createFunc(FuncName("event-trigger-func"))).id

            val creationResponse = httpTemplate.post("/v1/namespaces/539/triggers").body(
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
            verifyNoRequests(TriggerCreateRequested::class)
        }

        @Test
        fun `Tries to create trigger but topic does not exist`() {
            val funcId = awaitCompleted(createFunc(FuncName("event-trigger-func"))).id

            val creationResponse = httpTemplate.post("/v1/namespaces/539/triggers").body(
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
            verifyNoRequests(TriggerCreateRequested::class)
        }

        @Test
        fun `Tries to create trigger but func does not exists`() {
            val topicId = awaitCompleted(createTopic(TopicName("event-trigger-topic"))).id

            val creationResponse = httpTemplate.post("/v1/namespaces/539/triggers").body(
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
            verifyNoRequests(TriggerCreateRequested::class)
        }
    }

    @Nested
    inner class HookTriggerTest {
        @Test
        fun `Creates trigger`() {
            val funcId = awaitCompleted(createFunc(FuncName("hook-trigger-func"))).id

            val creationResponse = httpTemplate.post("/v1/namespaces/539/triggers").body(
                ApiTriggerCreateReq(
                    type = TriggerType.Hook,
                    name = TriggerName("hook-trigger"),
                    funcId = funcId,
                    inputs = TriggerInputs()
                )
            ).execute()

            assertThat(creationResponse.statusCode, equalTo(Accepted))
            require(creationResponse is HttpSuccessResponse) { "request was not successful" }

            val result = creationResponse.result(ApiTriggerCreateRequested::class)
            awaitCompleted(result)

            with(triggerQueryRepository.get(result.id)) {
                assertThat(id, equalTo(result.id))
                assertThat(name, equalTo(TriggerName("hook-trigger")))
                assertThat(inputs, equalTo(TriggerInputs()))
                assertThat(this.funcId, equalTo(funcId))
                require(this is Trigger.Hook) { "not HookTrigger" }
            }
        }


        @Test
        fun `Creates triggers with different hookId, funcId combinations`() {
            val func_one = awaitCompleted(createFunc(FuncName("hook-trigger-func-one"))).id
            val func_two = awaitCompleted(createFunc(FuncName("hook-trigger-func-two"))).id

            awaitCompleted(
                createHookTrigger(
                    name = TriggerName("trigger3"),
                    funcId = func_two
                )
            )
            awaitCompleted(
                createHookTrigger(
                    name = TriggerName("trigger4"),
                    funcId = func_one
                )
            )


            assertThat(
                triggerQueryRepository.list(
                    TriggerQueryRepository.TriggerQuery(
                        workspaceIds = listOf(testWorkspace.id),
                        limit = Limit(10)
                    )
                ), hasSize(2)
            )
        }


        @Test
        fun `Tries to create trigger but func does not exists`() {

            val creationResponse = httpTemplate.post("/v1/namespaces/539/triggers").body(
                ApiTriggerCreateReq(
                    type = TriggerType.Hook,
                    name = TriggerName("hook-trigger"),
                    funcId = FuncId(1234),
                    inputs = TriggerInputs()
                )
            ).execute()

            assertThat(creationResponse.statusCode, equalTo(NotFound))
            require(creationResponse is HttpErrorResponse) { "request was successful" }

            val result = creationResponse.error(ApiError::class)
            assertThat(result.message, equalTo("Func not found"))
            verifyNoRequests(TriggerCreateRequested::class)
        }
    }

    @Nested
    inner class CronTriggerTest {
        @Test
        fun `Creates Trigger`() {
            val funcId = awaitCompleted(createFunc(FuncName("cron-trigger-func"))).id

            val creationResponse = httpTemplate.post("/v1/namespaces/539/triggers").body(
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

            val result = creationResponse.result(ApiTriggerCreateRequested::class)
            awaitCompleted(result)

            with(triggerQueryRepository.get(result.id)) {
                assertThat(id, equalTo(result.id))
                assertThat(name, equalTo(TriggerName("cron-trigger")))
                assertThat(inputs, equalTo(TriggerInputs()))
                assertThat(this.funcId, equalTo(funcId))
                require(this is Trigger.Cron) { "not cron trigger" }
                assertThat(cron, equalTo(CronPattern("0 0 9-17 * * MON-FRI")))
            }
        }

        @Test
        fun `Tries to create trigger but cron expression is invalid`() {
            val funcId = awaitCompleted(createFunc(FuncName("cron-trigger-func"))).id
            val response = httpTemplate.post("/v1/namespaces/539/triggers").body(
                """{
                |"type":"Cron",
                |"name": "cron-trigger",
                |"funcId": "${funcId.stringValue}",
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
    ): ApiTriggerCreateRequested {
        val creationResponse = httpTemplate.post("/v1/namespaces/539/triggers").body(
            ApiTriggerCreateReq(
                type = TriggerType.Hook,
                name = name,
                funcId = funcId,
                inputs = TriggerInputs()
            )
        ).execute()

        assertThat(creationResponse.statusCode, equalTo(Accepted))
        require(creationResponse is HttpSuccessResponse) { "request was not successful" }
        return creationResponse.result(ApiTriggerCreateRequested::class)
    }
}

