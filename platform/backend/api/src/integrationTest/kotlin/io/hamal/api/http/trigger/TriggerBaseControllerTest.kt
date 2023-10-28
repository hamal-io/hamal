package io.hamal.api.http.trigger

import io.hamal.api.http.BaseControllerTest
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.*
import io.hamal.lib.sdk.toReq
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import kotlin.time.Duration.Companion.seconds

internal sealed class TriggerBaseControllerTest : BaseControllerTest() {

    fun createFunc(name: FuncName): ApiSubmittedReqImpl<FuncId> {
        val createTopicResponse = httpTemplate.post("/v1/namespaces/1/funcs")
            .body(
                ApiFuncCreateReq(
                    name = name,
                    inputs = FuncInputs(),
                    code = CodeValue("")
                )
            )
            .execute()

        assertThat(createTopicResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(createTopicResponse is HttpSuccessResponse) { "request was not successful" }

        return createTopicResponse.toReq()
    }

    fun createTopic(topicName: TopicName): ApiSubmittedReqImpl<TopicId> {
        val createTopicResponse = httpTemplate.post("/v1/namespaces/{namespaceId}/topics")
            .path("namespaceId", testNamespace.id)
            .body(ApiTopicCreateReq(topicName))
            .execute()

        assertThat(createTopicResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(createTopicResponse is HttpSuccessResponse) { "request was not successful" }

        return createTopicResponse.toReq()
    }

    fun createHook(hookName: HookName): ApiSubmittedReqImpl<HookId> {
        val createHookResponse = httpTemplate.post("/v1/namespaces/1/hooks")
            .path("groupId", testGroup.id)
            .body(ApiHookCreateReq(hookName))
            .execute()

        assertThat(createHookResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(createHookResponse is HttpSuccessResponse) { "request was not successful" }

        return createHookResponse.toReq()
    }

    fun createFixedRateTrigger(name: TriggerName): ApiSubmittedReqImpl<TriggerId> {
        val funcId = awaitCompleted(createFunc(FuncName(name.value))).id

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

        assertThat(creationResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(creationResponse is HttpSuccessResponse) { "request was not successful" }

        return creationResponse.toReq()
    }

    fun createTrigger(req: ApiTriggerCreateReq): ApiSubmittedReqImpl<TriggerId> {
        val creationResponse = httpTemplate.post("/v1/namespaces/1/triggers")
            .body(req)
            .execute()

        assertThat(creationResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(creationResponse is HttpSuccessResponse) { "request was not successful" }

        return creationResponse.toReq()
    }

    fun listTriggers(): ApiTriggerList {
        val listTriggersResponse = httpTemplate.get("/v1/triggers")
            .execute()

        assertThat(listTriggersResponse.statusCode, equalTo(HttpStatusCode.Ok))
        require(listTriggersResponse is HttpSuccessResponse) { "request was not successful" }
        return listTriggersResponse.result(ApiTriggerList::class)
    }

    fun getTrigger(triggerId: TriggerId): ApiTrigger {
        val listTriggersResponse = httpTemplate.get("/v1/triggers/{triggerId}")
            .path("triggerId", triggerId)
            .execute()

        assertThat(listTriggersResponse.statusCode, equalTo(HttpStatusCode.Ok))
        require(listTriggersResponse is HttpSuccessResponse) { "request was not successful" }
        return listTriggersResponse.result(ApiTrigger::class)
    }
}