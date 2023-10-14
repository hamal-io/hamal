package io.hamal.api.http.trigger

import io.hamal.api.http.BaseControllerTest
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import kotlin.time.Duration.Companion.seconds

internal sealed class TriggerBaseControllerTest : BaseControllerTest() {

    fun createFunc(name: FuncName): ApiSubmittedReqWithId {
        val createTopicResponse = httpTemplate.post("/v1/groups/{groupId}/funcs")
            .path("groupId", testGroup.id)
            .body(
                ApiCreateFuncReq(
                    namespaceId = null,
                    name = name,
                    inputs = FuncInputs(),
                    code = CodeValue("")
                )
            )
            .execute()

        assertThat(createTopicResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(createTopicResponse is SuccessHttpResponse) { "request was not successful" }

        return createTopicResponse.result(ApiSubmittedReqWithId::class)
    }

    fun createTopic(topicName: TopicName): ApiSubmittedReqWithId {
        val createTopicResponse = httpTemplate.post("/v1/groups/{groupId}/topics")
            .path("groupId", testGroup.id)
            .body(ApiCreateTopicReq(topicName))
            .execute()

        assertThat(createTopicResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(createTopicResponse is SuccessHttpResponse) { "request was not successful" }

        return createTopicResponse.result(ApiSubmittedReqWithId::class)
    }

    fun createHook(hookName: HookName): ApiSubmittedReqWithId {
        val createHookResponse = httpTemplate.post("/v1/groups/{groupId}/hooks")
            .path("groupId", testGroup.id)
            .body(
                ApiCreateHookReq(
                    namespaceId = null,
                    name = hookName
                )
            )
            .execute()

        assertThat(createHookResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(createHookResponse is SuccessHttpResponse) { "request was not successful" }

        return createHookResponse.result(ApiSubmittedReqWithId::class)
    }

    fun createFixedRateTrigger(name: TriggerName): ApiSubmittedReqWithId {
        val funcId = awaitCompleted(createFunc(FuncName(name.value))).id(::FuncId)

        val creationResponse = httpTemplate.post("/v1/triggers")
            .path("groupId", testGroup.id)
            .body(
                ApiCreateTriggerReq(
                    type = TriggerType.FixedRate,
                    name = name,
                    funcId = funcId,
                    inputs = TriggerInputs(),
                    duration = 10.seconds,
                )
            )
            .execute()

        assertThat(creationResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(creationResponse is SuccessHttpResponse) { "request was not successful" }

        return creationResponse.result(ApiSubmittedReqWithId::class)
    }

    fun createTrigger(req: ApiCreateTriggerReq): ApiSubmittedReqWithId {
        val creationResponse = httpTemplate.post("/v1/triggers")
            .path("groupId", testGroup.id)
            .body(req)
            .execute()

        assertThat(creationResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(creationResponse is SuccessHttpResponse) { "request was not successful" }

        return creationResponse.result(ApiSubmittedReqWithId::class)
    }

    fun listTriggers(): ApiTriggerList {
        val listTriggersResponse = httpTemplate.get("/v1/groups/{groupId}/triggers")
            .path("groupId", testGroup.id)
            .execute()

        assertThat(listTriggersResponse.statusCode, equalTo(HttpStatusCode.Ok))
        require(listTriggersResponse is SuccessHttpResponse) { "request was not successful" }
        return listTriggersResponse.result(ApiTriggerList::class)
    }

    fun getTrigger(triggerId: TriggerId): ApiTrigger {
        val listTriggersResponse = httpTemplate.get("/v1/triggers/{triggerId}")
            .path("triggerId", triggerId)
            .execute()

        assertThat(listTriggersResponse.statusCode, equalTo(HttpStatusCode.Ok))
        require(listTriggersResponse is SuccessHttpResponse) { "request was not successful" }
        return listTriggersResponse.result(ApiTrigger::class)
    }
}