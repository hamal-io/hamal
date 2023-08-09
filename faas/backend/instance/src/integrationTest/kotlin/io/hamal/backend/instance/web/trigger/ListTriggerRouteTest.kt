package io.hamal.backend.instance.web.trigger

import io.hamal.lib.domain.vo.TriggerName
import io.hamal.lib.sdk.domain.ApiTriggerList
import io.hamal.lib.sdk.extension.parameter
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class ListTriggerRouteTest : BaseTriggerRouteTest() {
    @Test
    fun `No trigger to list`() {
        with(listTriggers()) {
            assertThat(triggers, empty())
        }
    }

    @Test
    fun `Single trigger`() {
        val creationResponse = awaitCompleted(createFixedRateTrigger(TriggerName("trigger-one")))

        with(listTriggers()) {
            assertThat(triggers, hasSize(1))
            with(triggers.first()) {
                assertThat(id, equalTo(creationResponse.id))
                assertThat(name, equalTo(TriggerName("trigger-one")))
            }
        }
    }

    @Test
    fun `Limit triggers`() {
        awaitCompleted(
            IntRange(0, 20).map { createFixedRateTrigger(TriggerName("trigger-$it")) }
        )

        val listResponse = httpTemplate.get("/v1/triggers")
            .parameter("limit", 12)
            .execute(ApiTriggerList::class)

        assertThat(listResponse.triggers, hasSize(12))

        listResponse.triggers.forEachIndexed { idx, trigger ->
            assertThat(trigger.name, equalTo(TriggerName("trigger-${(20 - idx)}")))
        }
    }

    @Test
    fun `Skip and limit triggers`() {
        val requests = IntRange(0, 20).map { createFixedRateTrigger(TriggerName("trigger-$it")) }
        awaitCompleted(requests)

        val request15 = requests[15]

        val listResponse = (httpTemplate.get("/v1/triggers")
            .parameter("after_id", request15.id)
            .parameter("limit", 1))
            .execute(ApiTriggerList::class)

        with(listResponse) {
            assertThat(triggers, hasSize(1))
            assertThat(triggers.first().name, equalTo(TriggerName("trigger-14")))
        }
    }
}