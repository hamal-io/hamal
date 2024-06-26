package io.hamal.api.http.controller.trigger


import io.hamal.lib.domain._enum.TriggerStates.Active
import io.hamal.lib.domain._enum.TriggerStates.Inactive
import io.hamal.lib.domain.vo.NamespaceName.Companion.NamespaceName
import io.hamal.lib.domain.vo.TriggerId.Companion.TriggerId
import io.hamal.lib.domain.vo.TriggerName.Companion.TriggerName
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.sdk.api.ApiError
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

internal class TriggerStatesControllerTest : TriggerBaseControllerTest() {

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

        assertThat(req.statusCode, equalTo(HttpStatusCode.NotFound))
        require(req is HttpErrorResponse) { "request was successful" }

        val result = req.error(ApiError::class)
        assertThat(result.message, equalTo("Trigger not found"))
        verifyNoRequests()
    }
}
