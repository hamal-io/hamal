package io.hamal.core.request.handler.state

import io.hamal.core.request.handler.BaseRequestHandlerTest
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.State
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.StateSetRequested
import io.hamal.lib.domain.vo.*
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired


internal class StateSetHandlerTest : BaseRequestHandlerTest() {
    @Test
    fun `Set state`() {
        val correlation = Correlation(
            id = CorrelationId.default,
            funcId = FuncId(3)
        )

        testInstance(
            StateSetRequested(
                requestId = RequestId(1),
                requestStatus = RequestStatus.Submitted,
                requestedBy = AuthId(2),
                state = CorrelatedState(
                    correlation = correlation,
                    value = State(HotObject.builder().set("Hamal", "Rocks").build())
                ),
                workspaceId = WorkspaceId(4)
            )
        )

        with(stateQueryRepository.get(correlation)) {
            assertThat(value, equalTo(State(HotObject.builder().set("Hamal", "Rocks").build())))
        }

    }

    @Autowired
    private lateinit var testInstance: StateSetHandler
}