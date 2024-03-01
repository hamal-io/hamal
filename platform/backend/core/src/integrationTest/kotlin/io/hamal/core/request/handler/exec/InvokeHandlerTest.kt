package io.hamal.core.request.handler.exec

import io.hamal.core.request.handler.BaseReqHandlerTest
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain.request.ExecInvokeRequested
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.ExecQueryRepository.ExecQuery
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired


internal class ExecInvokeHandlerTest : BaseReqHandlerTest() {

    @Test
    fun `Invokes execution with code`() {
        testInstance(
            ExecInvokeRequested(
                id = RequestId(1),
                by = AuthId(2),
                status = Submitted,
                execId = ExecId(3333),
                namespaceId = testNamespace.id,
                workspaceId = testWorkspace.id,
                inputs = InvocationInputs(HotObject.builder().set("hamal", "justworks").build()),
                code = ExecCode(value = CodeValue("code")),
                funcId = null,
                correlationId = null,
                invocation = Invocation.Event(listOf())
            )
        )

        execQueryRepository.list(ExecQuery(workspaceIds = listOf())).also {
            assertThat(it, hasSize(1))

            with(it.first()) {
                assertThat(id, equalTo(ExecId(3333)))
                assertThat(correlation, nullValue())
                assertThat(inputs, equalTo(ExecInputs(HotObject.builder().set("hamal", "justworks").build())))
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
                HotObject.builder()
                    .set("override", "false")
                    .set("func", "func")
                    .build()
            )
        )
        testInstance(
            ExecInvokeRequested(
                id = RequestId(1),
                by = AuthId(2),
                correlationId = CorrelationId("some-correlation"),
                status = Submitted,
                execId = ExecId(3333),
                namespaceId = testNamespace.id,
                workspaceId = testWorkspace.id,
                inputs = InvocationInputs(
                    HotObject.builder()
                        .set("override", "true")
                        .set("invocation", "invocation")
                        .build()
                ),
                funcId = FuncId(4444),
                code = ExecCode(
                    id = CodeId(4455),
                    version = CodeVersion(5544),
                ),
                invocation = Invocation.Event(listOf())
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
                            HotObject.builder()
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
            id = RequestId(1),
            by = AuthId(2),
            correlationId = CorrelationId("some-correlation"),
            status = Submitted,
            execId = ExecId(3333),
            namespaceId = testNamespace.id,
            workspaceId = testWorkspace.id,
            inputs = InvocationInputs(
                HotObject.builder()
                    .set("override", "true")
                    .set("invocation", "invocation")
                    .build()),
            funcId = FuncId(4444),
            code = ExecCode(
                id = CodeId(5555),
                version = CodeVersion(6666),
            ),
            invocation = Invocation.Event(listOf())
        )
    }
    //@formatter:on
}