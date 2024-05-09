package io.hamal.api.http.controller.trigger

import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.domain._enum.TriggerTypes.FixedRate
import io.hamal.lib.domain.vo.FuncName.Companion.FuncName
import io.hamal.lib.domain.vo.TriggerDuration.Companion.TriggerDuration
import io.hamal.lib.domain.vo.TriggerInputs
import io.hamal.lib.domain.vo.TriggerName.Companion.TriggerName
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiTriggerCreateReq
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.seconds

internal class TriggerDeleteControllerTest : TriggerBaseControllerTest() {

    @Test
    fun `Deletes a trigger`() {
        val someFuncId = awaitCompleted(createFunc(FuncName("some-func-to-trigger"))).id
        val triggerId = awaitCompleted(
            createTrigger(
                ApiTriggerCreateReq(
                    type = FixedRate,
                    correlationId = null,
                    name = TriggerName("trigger-one"),
                    inputs = TriggerInputs(ValueObject.builder().set("hamal", "rocks").build()),
                    funcId = someFuncId,
                    duration = TriggerDuration(10.seconds.toIsoString())
                )
            )
        ).id

        val deleteResponse = awaitCompleted(deleteTrigger(triggerId))
        assertThat(deleteResponse.id, equalTo(triggerId))
        assertThat(listTriggers().triggers, empty())
        assertThat(funcQueryRepository.get(someFuncId).name, equalTo(FuncName("some-func-to-trigger")))
    }

    @Test
    fun `Tries to delete a trigger that already has been deleted`() {
        val triggerId = awaitCompleted(createFixedRateTrigger(TriggerName("trigger-one"))).id

        awaitCompleted(deleteTrigger(triggerId))

        val deleteTriggerResponse = httpTemplate.delete("/v1/triggers/$triggerId").execute()
        assertThat(deleteTriggerResponse.statusCode, equalTo(NotFound))
        require(deleteTriggerResponse is HttpErrorResponse) { "request was successful" }

        val error = deleteTriggerResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Trigger not found"))
    }

    @Test
    fun `Tries to delete a trigger which does not exists`() {
        val deleteTriggerResponse = httpTemplate.delete("/v1/triggers/42").execute()
        assertThat(deleteTriggerResponse.statusCode, equalTo(NotFound))
        require(deleteTriggerResponse is HttpErrorResponse) { "request was successful" }

        val error = deleteTriggerResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Trigger not found"))
    }
}