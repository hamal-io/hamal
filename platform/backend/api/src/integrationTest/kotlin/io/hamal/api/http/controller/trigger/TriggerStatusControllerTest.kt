package io.hamal.api.http.controller.trigger

import io.hamal.lib.domain._enum.TriggerStatus.Active
import io.hamal.lib.domain._enum.TriggerStatus.Inactive
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiTriggerCreateReq
import io.hamal.lib.sdk.api.ApiTriggerCreateSubmitted
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.seconds

internal class TriggerStatusControllerTest : TriggerBaseControllerTest() {

    @Test
    fun `Default status is active`() {
        val triggerId = createFixedTrigger()
        with(getTrigger(triggerId)) {
            assertThat(id, Matchers.equalTo(triggerId))
            assertThat(name, Matchers.equalTo(TriggerName("trigger")))
            assertThat(flow.name, Matchers.equalTo(FlowName("hamal")))
            assertThat(status, equalTo(Active))
        }
    }

    @Test
    fun `Deactivates a trigger`() {
        val triggerId = createFixedTrigger()
        awaitCompleted(deactivateTrigger(triggerId))

        with(getTrigger(triggerId)) {
            assertThat(id, Matchers.equalTo(triggerId))
            assertThat(name, Matchers.equalTo(TriggerName("trigger")))
            assertThat(flow.name, Matchers.equalTo(FlowName("hamal")))
            assertThat(status, equalTo(Inactive))
        }

        awaitCompleted(activateTrigger(triggerId))

        with(getTrigger(triggerId)) {
            assertThat(id, Matchers.equalTo(triggerId))
            assertThat(name, Matchers.equalTo(TriggerName("trigger")))
            assertThat(flow.name, Matchers.equalTo(FlowName("hamal")))
            assertThat(status, equalTo(Active))
        }
    }

    @Test
    fun `Activates an active trigger`() {
        val triggerId = createFixedTrigger()
        awaitCompleted(activateTrigger(triggerId))

        with(getTrigger(triggerId)) {
            assertThat(id, Matchers.equalTo(triggerId))
            assertThat(name, Matchers.equalTo(TriggerName("trigger")))
            assertThat(flow.name, Matchers.equalTo(FlowName("hamal")))
            assertThat(status, equalTo(Active))
        }

    }

    @Test
    fun `Tries to activate a trigger that does not exist`() {
        val req = httpTemplate.post("/v1/trigger/{triggerId}/activate")
            .path("triggerId", TriggerId(123))
            .execute()

        assertThat(req.statusCode, Matchers.equalTo(HttpStatusCode.NotFound))
        require(req is HttpErrorResponse) { "request was successful" }

        val result = req.error(ApiError::class)
        assertThat(result.message, Matchers.equalTo("Trigger not found"))
        verifyNoRequests()
    }
}

private fun TriggerStatusControllerTest.createFixedTrigger(): TriggerId {
    val funcId = awaitCompleted(createFunc(FuncName("fixed-trigger-func"))).funcId

    val creationResponse = httpTemplate.post("/v1/flows/1/triggers").body(
        ApiTriggerCreateReq(
            type = TriggerType.FixedRate,
            name = TriggerName("trigger"),
            funcId = funcId,
            inputs = TriggerInputs(),
            duration = 10.seconds,
        )
    ).execute()

    assertThat(creationResponse.statusCode, Matchers.equalTo(HttpStatusCode.Accepted))
    require(creationResponse is HttpSuccessResponse) { "request was not successful" }

    val result = creationResponse.result(ApiTriggerCreateSubmitted::class)
    awaitCompleted(result)
    return result.triggerId
}