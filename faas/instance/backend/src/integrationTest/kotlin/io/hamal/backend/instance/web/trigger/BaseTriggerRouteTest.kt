package io.hamal.backend.instance.web.trigger

import io.hamal.backend.instance.web.BaseRouteTest
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.req.CreateFuncReq
import io.hamal.lib.domain.req.CreateTopicReq
import io.hamal.lib.domain.req.CreateTriggerReq
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.sdk.domain.ApiSubmittedReqWithId
import io.hamal.lib.sdk.domain.ApiTrigger
import io.hamal.lib.sdk.domain.ApiTriggerList
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import kotlin.time.Duration.Companion.seconds

internal sealed class BaseTriggerRouteTest : BaseRouteTest() {

    fun createFunc(name: FuncName): ApiSubmittedReqWithId {
        val createTopicResponse = httpTemplate.post("/v1/funcs")
            .body(
                CreateFuncReq(
                    namespaceId = null,
                    name = name,
                    inputs = FuncInputs(),
                    code = CodeType("")
                )
            )
            .execute()

        assertThat(createTopicResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(createTopicResponse is SuccessHttpResponse) { "request was not successful" }

        return createTopicResponse.result(ApiSubmittedReqWithId::class)
    }

    fun createTopic(topicName: TopicName): ApiSubmittedReqWithId {
        val createTopicResponse = httpTemplate.post("/v1/topics").body(CreateTopicReq(topicName)).execute()

        assertThat(createTopicResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(createTopicResponse is SuccessHttpResponse) { "request was not successful" }

        return createTopicResponse.result(ApiSubmittedReqWithId::class)
    }

    fun createFixedRateTrigger(name: TriggerName): ApiSubmittedReqWithId {
        val funcId = awaitCompleted(createFunc(FuncName(name.value))).id(::FuncId)

        val creationResponse = httpTemplate.post("/v1/triggers")
            .body(
                CreateTriggerReq(
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

    fun createTrigger(req: CreateTriggerReq): ApiSubmittedReqWithId {
        val creationResponse = httpTemplate.post("/v1/triggers")
            .body(req)
            .execute()

        assertThat(creationResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(creationResponse is SuccessHttpResponse) { "request was not successful" }

        return creationResponse.result(ApiSubmittedReqWithId::class)
    }

    fun listTriggers(): ApiTriggerList {
        val listTriggersResponse = httpTemplate.get("/v1/triggers").execute()
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