package io.hamal.api.http.controller.trigger

import io.hamal.api.http.controller.BaseControllerTest
import io.hamal.lib.domain._enum.TopicType
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import kotlin.time.Duration.Companion.seconds

internal sealed class TriggerBaseControllerTest : BaseControllerTest() {

    fun createFunc(name: FuncName): ApiFuncCreateRequested {
        val createTopicResponse = httpTemplate.post("/v1/namespaces/1/funcs")
            .body(
                ApiFuncCreateRequest(
                    name = name,
                    inputs = FuncInputs(),
                    code = CodeValue("")
                )
            )
            .execute()

        assertThat(createTopicResponse.statusCode, equalTo(Accepted))
        require(createTopicResponse is HttpSuccessResponse) { "request was not successful" }

        return createTopicResponse.result(ApiFuncCreateRequested::class)
    }

    fun createTopic(topicName: TopicName): ApiTopicCreateRequested {
        val createTopicResponse = httpTemplate.post("/v1/namespaces/1/topics")
            .body(ApiTopicCreateRequest(topicName, TopicType.Namespace))
            .execute()

        assertThat(createTopicResponse.statusCode, equalTo(Accepted))
        require(createTopicResponse is HttpSuccessResponse) { "request was not successful" }

        return createTopicResponse.result(ApiTopicCreateRequested::class)
    }

    fun createHook(hookName: HookName): ApiHookCreateRequested {
        val createHookResponse = httpTemplate.post("/v1/namespaces/1/hooks")
            .body(ApiHookCreateRequest(hookName))
            .execute()

        assertThat(createHookResponse.statusCode, equalTo(Accepted))
        require(createHookResponse is HttpSuccessResponse) { "request was not successful" }

        return createHookResponse.result(ApiHookCreateRequested::class)
    }

    fun createFixedRateTrigger(name: TriggerName): ApiTriggerCreateRequested {
        val funcId = awaitCompleted(createFunc(FuncName(name.value))).funcId

        val creationResponse = httpTemplate.post("/v1/namespaces/1/triggers")
            .body(
                ApiTriggerCreateReq(
                    type = TriggerType.FixedRate,
                    name = name,
                    funcId = funcId,
                    inputs = TriggerInputs(),
                    duration = TriggerDuration(10.seconds.toIsoString()),
                )
            )
            .execute()

        assertThat(creationResponse.statusCode, equalTo(Accepted))
        require(creationResponse is HttpSuccessResponse) { "request was not successful" }

        return creationResponse.result(ApiTriggerCreateRequested::class)
    }

    fun createTrigger(req: ApiTriggerCreateReq): ApiTriggerCreateRequested {
        val creationResponse = httpTemplate.post("/v1/namespaces/1/triggers")
            .body(req)
            .execute()

        assertThat(creationResponse.statusCode, equalTo(Accepted))
        require(creationResponse is HttpSuccessResponse) { "request was not successful" }

        return creationResponse.result(ApiTriggerCreateRequested::class)
    }

    fun listTriggers(): ApiTriggerList {
        val listTriggersResponse = httpTemplate.get("/v1/triggers")
            .execute()

        assertThat(listTriggersResponse.statusCode, equalTo(Ok))
        require(listTriggersResponse is HttpSuccessResponse) { "request was not successful" }
        return listTriggersResponse.result(ApiTriggerList::class)
    }

    fun getTrigger(triggerId: TriggerId): ApiTrigger {
        val listTriggersResponse = httpTemplate.get("/v1/triggers/{triggerId}")
            .path("triggerId", triggerId)
            .execute()

        assertThat(listTriggersResponse.statusCode, equalTo(Ok))
        require(listTriggersResponse is HttpSuccessResponse) { "request was not successful" }
        return listTriggersResponse.result(ApiTrigger::class)
    }

    fun activateTrigger(triggerId: TriggerId): ApiTriggerStatusRequested {
        val res = httpTemplate.post("/v1/trigger/{triggerId}/activate")
            .path("triggerId", triggerId)
            .execute()

        assertThat(res.statusCode, equalTo(Accepted))
        require(res is HttpSuccessResponse) { "request was not successful" }
        return res.result(ApiTriggerStatusRequested::class)
    }

    fun deactivateTrigger(triggerId: TriggerId): ApiTriggerStatusRequested {
        val res = httpTemplate.post("/v1/trigger/{triggerId}/deactivate")
            .path("triggerId", triggerId)
            .execute()

        assertThat(res.statusCode, equalTo(Accepted))
        require(res is HttpSuccessResponse) { "request was not successful" }
        return res.result(ApiTriggerStatusRequested::class)
    }
}