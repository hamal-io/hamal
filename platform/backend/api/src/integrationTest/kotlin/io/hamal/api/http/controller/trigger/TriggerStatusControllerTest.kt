package io.hamal.api.http.controller.trigger


import io.hamal.lib.domain._enum.TriggerStatus.Active
import io.hamal.lib.domain._enum.TriggerStatus.Inactive
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.domain.vo.TriggerName
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.sdk.api.ApiError
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test

internal class TriggerStatusControllerTest : TriggerBaseControllerTest() {

    @Test
    fun `Default status is active`() {
        val triggerId = awaitCompleted(
            createFixedRateTrigger(
                name = TriggerName("trigger")
            )
        ).id
        with(getTrigger(triggerId)) {
            assertThat(id, equalTo(triggerId))
            assertThat(name, equalTo(TriggerName("trigger")))
            assertThat(namespace.name, equalTo(NamespaceName("hamal")))
            assertThat(status, equalTo(Active))
        }
    }

    @Test
    fun `Deactivates a trigger`() {
        val triggerId = awaitCompleted(
            createFixedRateTrigger(
                name = TriggerName("trigger")
            )
        ).id

        awaitCompleted(deactivateTrigger(triggerId))

        with(getTrigger(triggerId)) {
            assertThat(id, equalTo(triggerId))
            assertThat(name, equalTo(TriggerName("trigger")))
            assertThat(namespace.name, equalTo(NamespaceName("hamal")))
            assertThat(status, equalTo(Inactive))
        }

        awaitCompleted(activateTrigger(triggerId))

        with(getTrigger(triggerId)) {
            assertThat(id, equalTo(triggerId))
            assertThat(name, equalTo(TriggerName("trigger")))
            assertThat(namespace.name, equalTo(NamespaceName("hamal")))
            assertThat(status, equalTo(Active))
        }
    }

    @Test
    fun `Activates an active trigger`() {
        val triggerId = awaitCompleted(
            createFixedRateTrigger(
                name = TriggerName("trigger")
            )
        ).id

        awaitCompleted(activateTrigger(triggerId))

        with(getTrigger(triggerId)) {
            assertThat(id, equalTo(triggerId))
            assertThat(name, equalTo(TriggerName("trigger")))
            assertThat(namespace.name, equalTo(NamespaceName("hamal")))
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
