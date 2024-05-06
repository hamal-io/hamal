package io.hamal.core.request.handler.state

import io.hamal.core.request.handler.BaseRequestHandlerTest
import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.State
import io.hamal.lib.domain._enum.RequestStatuses.Submitted
import io.hamal.lib.domain.request.StateSetRequested
import io.hamal.lib.domain.vo.AuthId.Companion.AuthId
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId.Companion.FuncId
import io.hamal.lib.domain.vo.RequestId.Companion.RequestId
import io.hamal.lib.domain.vo.RequestStatus.Companion.RequestStatus
import io.hamal.lib.domain.vo.WorkspaceId.Companion.WorkspaceId
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
                requestStatus = RequestStatus(Submitted),
                requestedBy = AuthId(2),
                state = CorrelatedState(
                    correlation = correlation,
                    value = State(ValueObject.builder().set("Hamal", "Rocks").build())
                ),
                workspaceId = WorkspaceId(4)
            )
        )

        with(stateQueryRepository.get(correlation)) {
            assertThat(value, equalTo(State(ValueObject.builder().set("Hamal", "Rocks").build())))
        }

    }

    @Autowired
    private lateinit var testInstance: StateSetHandler
}