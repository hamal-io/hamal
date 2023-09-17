package io.hamal.api.web.trigger

import io.hamal.api.web.BaseControllerTest
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.sdk.hub.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import kotlin.time.Duration.Companion.seconds

internal sealed class BaseTriggerControllerTest : BaseControllerTest() {

    fun createFunc(name: FuncName): HubSubmittedReqWithId {
        val createTopicResponse = httpTemplate.post("/v1/groups/{groupId}/funcs")
            .path("groupId", testGroup.id)
            .body(
                HubCreateFuncReq(
                    namespaceId = null,
                    name = name,
                    inputs = FuncInputs(),
                    code = CodeType("")
                )
            )
            .execute()

        assertThat(createTopicResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(createTopicResponse is SuccessHttpResponse) { "request was not successful" }

        return createTopicResponse.result(HubSubmittedReqWithId::class)
    }

    fun createTopic(topicName: TopicName): HubSubmittedReqWithId {
        val createTopicResponse = httpTemplate.post("/v1/groups/{groupId}/topics")
            .path("groupId", testGroup.id)
            .body(HubCreateTopicReq(topicName))
            .execute()

        assertThat(createTopicResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(createTopicResponse is SuccessHttpResponse) { "request was not successful" }

        return createTopicResponse.result(HubSubmittedReqWithId::class)
    }

    fun createFixedRateTrigger(name: TriggerName): HubSubmittedReqWithId {
        val funcId = awaitCompleted(createFunc(FuncName(name.value))).id(::FuncId)

        val creationResponse = httpTemplate.post("/v1/triggers")
            .body(
                HubCreateTriggerReq(
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

        return creationResponse.result(HubSubmittedReqWithId::class)
    }

    fun createTrigger(req: HubCreateTriggerReq): HubSubmittedReqWithId {
        val creationResponse = httpTemplate.post("/v1/triggers")
            .body(req)
            .execute()

        assertThat(creationResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(creationResponse is SuccessHttpResponse) { "request was not successful" }

        return creationResponse.result(HubSubmittedReqWithId::class)
    }

    fun listTriggers(): HubTriggerList {
        val listTriggersResponse = httpTemplate.get("/v1/groups/{groupId}/triggers")
            .path("groupId", testGroup.id)
            .execute()

        assertThat(listTriggersResponse.statusCode, equalTo(HttpStatusCode.Ok))
        require(listTriggersResponse is SuccessHttpResponse) { "request was not successful" }
        return listTriggersResponse.result(HubTriggerList::class)
    }

    fun getTrigger(triggerId: TriggerId): HubTrigger {
        val listTriggersResponse = httpTemplate.get("/v1/triggers/{triggerId}")
            .path("triggerId", triggerId)
            .execute()

        assertThat(listTriggersResponse.statusCode, equalTo(HttpStatusCode.Ok))
        require(listTriggersResponse is SuccessHttpResponse) { "request was not successful" }
        return listTriggersResponse.result(HubTrigger::class)
    }
}