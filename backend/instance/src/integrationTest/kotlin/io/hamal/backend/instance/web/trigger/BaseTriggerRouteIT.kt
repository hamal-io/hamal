package io.hamal.backend.instance.web.trigger

import io.hamal.backend.instance.web.BaseRouteIT
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.req.CreateFuncReq
import io.hamal.lib.domain.req.CreateTriggerReq
import io.hamal.lib.domain.req.SubmittedCreateFuncReq
import io.hamal.lib.domain.req.SubmittedCreateTriggerReq
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.domain.ListTriggersResponse
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import kotlin.time.Duration.Companion.seconds

internal sealed class BaseTriggerRouteIT : BaseRouteIT() {

    fun createFunc(name: FuncName): SubmittedCreateFuncReq {
        val createTopicResponse = httpTemplate.post("/v1/funcs")
            .body(
                CreateFuncReq(
                    name = name,
                    inputs = FuncInputs(),
                    secrets = FuncSecrets(),
                    code = Code("")
                )
            )
            .execute()

        assertThat(createTopicResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(createTopicResponse is SuccessHttpResponse) { "request was not successful" }

        return createTopicResponse.result(SubmittedCreateFuncReq::class)
    }

    fun createFixedRateTrigger(name: TriggerName): SubmittedCreateTriggerReq {
        val funcResponse = createFunc(FuncName(name.value)).also { awaitReqCompleted(it.id) }
        val creationResponse = httpTemplate.post("/v1/triggers")
            .body(
                CreateTriggerReq(
                    type = TriggerType.FixedRate,
                    name = name,
                    funcId = funcResponse.funcId,
                    inputs = TriggerInputs(),
                    secrets = TriggerSecrets(),
                    duration = 10.seconds,
                )
            )
            .execute()

        assertThat(creationResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(creationResponse is SuccessHttpResponse) { "request was not successful" }

        return creationResponse.result(SubmittedCreateTriggerReq::class)
    }

    fun listTriggers(): ListTriggersResponse {
        val listTriggersResponse = httpTemplate.get("/v1/triggers").execute()
        assertThat(listTriggersResponse.statusCode, equalTo(HttpStatusCode.Ok))
        require(listTriggersResponse is SuccessHttpResponse) { "request was not successful" }
        return listTriggersResponse.result(ListTriggersResponse::class)
    }

}