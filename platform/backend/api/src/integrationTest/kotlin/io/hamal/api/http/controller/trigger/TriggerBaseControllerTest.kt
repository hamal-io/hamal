package io.hamal.api.http.controller.trigger

import io.hamal.api.http.controller.BaseControllerTest
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

    fun createFunc(name: FuncName): ApiFuncCreateSubmitted {
        val createTopicResponse = httpTemplate.post("/v1/namespaces/1/funcs")
            .body(
                ApiFuncCreateReq(
                    name = name,
                    inputs = FuncInputs(),
                    code = CodeValue("")
                )
            )
            .execute()

        assertThat(createTopicResponse.statusCode, equalTo(Accepted))
        require(createTopicResponse is HttpSuccessResponse) { "request was not successful" }

        return createTopicResponse.result(ApiFuncCreateSubmitted::class)
    }

    fun createTopic(topicName: TopicName): ApiTopicCreateSubmitted {
        val createTopicResponse = httpTemplate.post("/v1/namespaces/{namespaceId}/topics")
            .path("namespaceId", testNamespace.id)
            .body(ApiTopicCreateReq(topicName))
            .execute()

        assertThat(createTopicResponse.statusCode, equalTo(Accepted))
        require(createTopicResponse is HttpSuccessResponse) { "request was not successful" }

        return createTopicResponse.result(ApiTopicCreateSubmitted::class)
    }

    fun createHook(hookName: HookName): ApiHookCreateSubmitted {
        val createHookResponse = httpTemplate.post("/v1/namespaces/1/hooks")
            .path("groupId", testGroup.id)
            .body(ApiHookCreateReq(hookName))
            .execute()

        assertThat(createHookResponse.statusCode, equalTo(Accepted))
        require(createHookResponse is HttpSuccessResponse) { "request was not successful" }

        return createHookResponse.result(ApiHookCreateSubmitted::class)
    }

    fun createFixedRateTrigger(name: TriggerName): ApiTriggerCreateSubmitted {
        val funcId = awaitCompleted(createFunc(FuncName(name.value))).funcId

        val creationResponse = httpTemplate.post("/v1/namespaces/1/triggers")
            .body(
                ApiTriggerCreateReq(
                    type = TriggerType.FixedRate,
                    name = name,
                    funcId = funcId,
                    inputs = TriggerInputs(),
                    duration = 10.seconds,
                )
            )
            .execute()

        assertThat(creationResponse.statusCode, equalTo(Accepted))
        require(creationResponse is HttpSuccessResponse) { "request was not successful" }

        return creationResponse.result(ApiTriggerCreateSubmitted::class)
    }

    fun createTrigger(req: ApiTriggerCreateReq): ApiTriggerCreateSubmitted {
        val creationResponse = httpTemplate.post("/v1/namespaces/1/triggers")
            .body(req)
            .execute()

        assertThat(creationResponse.statusCode, equalTo(Accepted))
        require(creationResponse is HttpSuccessResponse) { "request was not successful" }

        return creationResponse.result(ApiTriggerCreateSubmitted::class)
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
}