package io.hamal.backend.instance.web.trigger

import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.req.CreateTriggerReq
import io.hamal.lib.domain.req.SubmittedCreateTriggerReq
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.TriggerInputs
import io.hamal.lib.domain.vo.TriggerName
import io.hamal.lib.domain.vo.TriggerSecrets
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.seconds

internal class CreateTriggerRouteIT : BaseTriggerRouteIT() {

    //func does not exist
    @Test
    fun `Creates fixed rate trigger`() {
        val creationResponse = httpTemplate.post("/v1/triggers")
            .body(
                CreateTriggerReq(
                    type = TriggerType.FixedRate,
                    name = TriggerName("fixed-rate-trigger"),
                    funcId = FuncId(0),
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
    }
}
