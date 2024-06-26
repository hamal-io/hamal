package io.hamal.core.request.handler.exec

import io.hamal.core.request.handler.BaseRequestHandlerTest
import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain._enum.RequestStatuses.Submitted
import io.hamal.lib.domain.request.ExecInvokeRequested
import io.hamal.lib.domain.vo.AuthId.Companion.AuthId
import io.hamal.lib.domain.vo.CodeId.Companion.CodeId
import io.hamal.lib.domain.vo.CodeValue.Companion.CodeValue
import io.hamal.lib.domain.vo.CodeVersion.Companion.CodeVersion
import io.hamal.lib.domain.vo.CorrelationId.Companion.CorrelationId
import io.hamal.lib.domain.vo.ExecCode
import io.hamal.lib.domain.vo.ExecId.Companion.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.FuncId.Companion.FuncId
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.RequestId.Companion.RequestId
import io.hamal.lib.domain.vo.RequestStatus.Companion.RequestStatus
import io.hamal.lib.domain.vo.TriggerId.Companion.TriggerId
import io.hamal.repository.api.ExecQueryRepository.ExecQuery
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired


internal class ExecInvokeHandlerTest : BaseRequestHandlerTest() {

    @Test
    fun `Invokes execution with code`() {
        testInstance(
            ExecInvokeRequested(
                requestId = RequestId(1),
                requestedBy = AuthId(2),
                requestStatus = RequestStatus(Submitted),
                id = ExecId(3333),
                triggerId = TriggerId(4444),
                namespaceId = testNamespace.id,
                workspaceId = testWorkspace.id,
                inputs = InvocationInputs(ValueObject.builder().set("hamal", "justworks").build()),
                code = ExecCode(value = CodeValue("code")),
                funcId = null,
                correlationId = null
            )
        )

        execQueryRepository.list(ExecQuery(workspaceIds = listOf())).also {
            assertThat(it, hasSize(1))

            with(it.first()) {
                assertThat(id, equalTo(ExecId(3333)))
                assertThat(correlation, nullValue())
                assertThat(inputs, equalTo(ExecInputs(ValueObject.builder().set("hamal", "justworks").build())))
                assertThat(code, equalTo(ExecCode(value = CodeValue("code"))))
            }
        }
    }

    @Test
    fun `Invokes execution with codeId and codeVersion`() {
        createFunc(
            id = FuncId(4444),
            codeId = CodeId(4455),
            codeVersion = CodeVersion(5544),
            inputs = FuncInputs(
                ValueObject.builder()
                    .set("override", "false")
                    .set("func", "func")
                    .build()
            )
        )
        testInstance(
            ExecInvokeRequested(
                requestId = RequestId(1),
                requestedBy = AuthId(2),
                correlationId = CorrelationId("some-correlation"),
                requestStatus = RequestStatus(Submitted),
                id = ExecId(3333),
                triggerId = TriggerId(4444),
                namespaceId = testNamespace.id,
                workspaceId = testWorkspace.id,
                inputs = InvocationInputs(
                    ValueObject.builder()
                        .set("override", "true")
                        .set("invocation", "invocation")
                        .build()
                ),
                funcId = FuncId(4444),
                code = ExecCode(
                    id = CodeId(4455),
                    version = CodeVersion(5544),
                )
            )
        )

        execQueryRepository.list(ExecQuery(workspaceIds = listOf())).also {
            assertThat(it, hasSize(1))

            with(it.first()) {
                assertThat(id, equalTo(ExecId(3333)))
                assertThat(
                    correlation,
                    equalTo(Correlation(funcId = FuncId(4444), id = CorrelationId("some-correlation")))
                )
                assertThat(
                    inputs, equalTo(
                        ExecInputs(
                            ValueObject.builder()
                                .set("override", "true")
                                .set("func", "func")
                                .set("invocation", "invocation")
                                .build()
                        )
                    )
                )
                assertThat(
                    code, equalTo(
                        ExecCode(
                            id = CodeId(4455),
                            version = CodeVersion(5544)
                        )
                    )
                )
            }
        }
    }

    @Test
    fun `Tries to invoke exec but func does not exists`() {
        val exception = assertThrows<NoSuchElementException> { testInstance(submittedFixedRateInvocationReq) }
        assertThat(exception.message, equalTo("Func not found"))
        execQueryRepository.list(ExecQuery(workspaceIds = listOf())).also {
            assertThat(it, empty())
        }
    }

    @Autowired
    private lateinit var testInstance: ExecInvokeHandler

    //    @formatter:off
    private val submittedFixedRateInvocationReq by lazy {
        ExecInvokeRequested(
            requestId = RequestId(1),
            requestedBy = AuthId(2),
            correlationId = CorrelationId("some-correlation"),
            requestStatus = RequestStatus(Submitted),
            id = ExecId(3333),
            triggerId = TriggerId(4444),
            namespaceId = testNamespace.id,
            workspaceId = testWorkspace.id,
            inputs = InvocationInputs(
                ValueObject.builder()
                    .set("override", "true")
                    .set("invocation", "invocation")
                    .build()),
            funcId = FuncId(4444),
            code = ExecCode(
                id = CodeId(5555),
                version = CodeVersion(6666),
            )
        )
    }
    //@formatter:on
}