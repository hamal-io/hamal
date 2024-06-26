package io.hamal.repository

import io.hamal.lib.common.domain.CmdId.Companion.CmdId
import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.CorrelationId.Companion.CorrelationId
import io.hamal.lib.domain.vo.FuncId.Companion.FuncId
import io.hamal.repository.api.StateCmdRepository
import io.hamal.repository.api.StateRepository
import io.hamal.repository.fixture.AbstractUnitTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory

internal class StateRepositoryTest : AbstractUnitTest() {

    @Nested
    inner class GetTest {

        @TestFactory
        fun `Get state by correlation`() = runWith(StateRepository::class) {
            set(
                StateCmdRepository.SetCmd(
                    CmdId(1), CorrelatedState(
                        correlation = Correlation(
                            id = CorrelationId("SomeCorrelationId"),
                            funcId = FuncId(2)
                        ),
                        value = State(ValueObject.builder().set("hamal", "rocks").build())
                    )
                )
            )

            with(get(Correlation(CorrelationId("SomeCorrelationId"), FuncId(2)))) {
                assertThat(correlation.id, equalTo(CorrelationId("SomeCorrelationId")))
                assertThat(correlation.funcId, equalTo(FuncId(2)))
                assertThat(value, equalTo(State(ValueObject.builder().set("hamal", "rocks").build())))
            }
        }

        @TestFactory
        fun `Get state but func id does not exists`() = runWith(StateRepository::class) {
            set(
                StateCmdRepository.SetCmd(
                    CmdId(1), CorrelatedState(
                        correlation = Correlation(
                            id = CorrelationId("SomeCorrelationId"),
                            funcId = FuncId(2)
                        ),
                        value = State(ValueObject.builder().set("hamal", "rocks").build())
                    )
                )
            )

            val result = get(Correlation(CorrelationId("SomeCorrelationId"), FuncId(22222)))
            with(result) {
                assertThat(correlation.funcId, equalTo(FuncId(22222)))
                assertThat(correlation.id, equalTo(CorrelationId("SomeCorrelationId")))
                assertThat(value, equalTo(State(ValueObject.empty)))
            }
        }

        @TestFactory
        fun `Get state but correlation  id does not exists`() = runWith(StateRepository::class) {
            set(
                StateCmdRepository.SetCmd(
                    CmdId(1), CorrelatedState(
                        correlation = Correlation(
                            id = CorrelationId("SomeCorrelationId"),
                            funcId = FuncId(2)
                        ),
                        value = State(ValueObject.builder().set("hamal", "rocks").build())
                    )
                )
            )

            val result = get(Correlation(CorrelationId("AnotherCorrelation"), FuncId(2)))
            with(result) {
                assertThat(correlation.funcId, equalTo(FuncId(2)))
                assertThat(correlation.id, equalTo(CorrelationId("AnotherCorrelation")))
                assertThat(value, equalTo(State(ValueObject.empty)))
            }
        }
    }

    @Nested
    inner class FindTest {

        @TestFactory
        fun `Find state by correlation`() = runWith(StateRepository::class) {
            set(
                StateCmdRepository.SetCmd(
                    CmdId(1), CorrelatedState(
                        correlation = Correlation(
                            id = CorrelationId("SomeCorrelationId"),
                            funcId = FuncId(2)
                        ),
                        value = State(ValueObject.builder().set("hamal", "rocks").build())
                    )
                )
            )

            with(find(Correlation(CorrelationId("SomeCorrelationId"), FuncId(2)))!!) {
                assertThat(correlation.id, equalTo(CorrelationId("SomeCorrelationId")))
                assertThat(correlation.funcId, equalTo(FuncId(2)))
                assertThat(value, equalTo(State(ValueObject.builder().set("hamal", "rocks").build())))
            }
        }

        @TestFactory
        fun `Find state but func id does not exists`() = runWith(StateRepository::class) {
            set(
                StateCmdRepository.SetCmd(
                    CmdId(1), CorrelatedState(
                        correlation = Correlation(
                            id = CorrelationId("SomeCorrelationId"),
                            funcId = FuncId(2)
                        ),
                        value = State(ValueObject.builder().set("hamal", "rocks").build())
                    )
                )
            )

            val result = find(Correlation(CorrelationId("SomeCorrelationId"), FuncId(22222)))
            assertThat(result, nullValue())
        }

        @TestFactory
        fun `Find state but correlation  id does not exists`() = runWith(StateRepository::class) {
            set(
                StateCmdRepository.SetCmd(
                    CmdId(1), CorrelatedState(
                        correlation = Correlation(
                            id = CorrelationId("SomeCorrelationId"),
                            funcId = FuncId(2)
                        ),
                        value = State(ValueObject.builder().set("hamal", "rocks").build())
                    )
                )
            )

            val result = find(Correlation(CorrelationId("AnotherCorrelation"), FuncId(2)))
            assertThat(result, nullValue())
        }
    }
}