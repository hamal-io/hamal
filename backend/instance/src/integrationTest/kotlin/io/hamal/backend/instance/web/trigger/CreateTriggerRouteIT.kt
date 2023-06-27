package io.hamal.backend.instance.web.trigger

import io.hamal.lib.domain.FixedRateTrigger
import io.hamal.lib.domain.HamalError
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.req.CreateTriggerReq
import io.hamal.lib.domain.req.SubmittedCreateTriggerReq
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.seconds

internal class CreateTriggerRouteIT : BaseTriggerRouteIT() {

    @Test
    fun `Creates fixed rate trigger`() {
        val funcResponse = createFunc(FuncName("fixed-trigger-func"))
        val creationResponse = httpTemplate.post("/v1/triggers")
            .body(
                CreateTriggerReq(
                    type = TriggerType.FixedRate,
                    name = TriggerName("fixed-rate-trigger"),
                    funcId = funcResponse.funcId,
                    inputs = TriggerInputs(),
                    secrets = TriggerSecrets(),
                    duration = 10.seconds,
                )
            )
            .execute()
        Thread.sleep(10)

        assertThat(creationResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(creationResponse is SuccessHttpResponse) { "request was not successful" }

        val result = creationResponse.result(SubmittedCreateTriggerReq::class)
        verifyReqCompleted(result.id)

        with(triggerQueryService.get(result.triggerId)) {
            assertThat(id, equalTo(result.triggerId))
            assertThat(name, equalTo(TriggerName("fixed-rate-trigger")))
            assertThat(inputs, equalTo(TriggerInputs()))
            assertThat(secrets, equalTo(TriggerSecrets()))
            assertThat(funcId, equalTo(funcResponse.funcId))
            require(this is FixedRateTrigger) { "not FixedRateTrigger" }
            assertThat(duration, equalTo(10.seconds))
        }
    }

    @Test
    fun `Tries to create fixed rate trigger but func does not exist`() {
        val creationResponse = httpTemplate.post("/v1/triggers")
            .body(
                CreateTriggerReq(
                    type = TriggerType.FixedRate,
                    name = TriggerName("fixed-rate-trigger"),
                    funcId = FuncId(123),
                    inputs = TriggerInputs(),
                    secrets = TriggerSecrets(),
                    duration = 10.seconds,
                )
            )
            .execute()
        Thread.sleep(10)

        assertThat(creationResponse.statusCode, equalTo(HttpStatusCode.NotFound))
        require(creationResponse is ErrorHttpResponse) { "request was successful" }

        val result = creationResponse.error(HamalError::class)
        assertThat(result.message, equalTo("Func not found"))
        verifyNoRequests()
    }
}
