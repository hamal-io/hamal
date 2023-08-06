package io.hamal.backend.instance.req.handler.exec

import io.hamal.backend.instance.req.handler.BaseReqHandlerTest
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.req.ReqStatus
import io.hamal.lib.domain.req.SubmittedInvokeAdhocReq
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.kua.value.CodeValue
import io.hamal.lib.kua.value.StringValue
import io.hamal.lib.kua.value.TableValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class InvokeAdhocHandlerTest : BaseReqHandlerTest() {

    @Test
    fun `Invokes adhoc execution`() {
        testInstance(submittedInvocationReq)

        execQueryRepository.list { }.also {
            assertThat(it, hasSize(1))

            with(it.first()) {
                assertThat(id, equalTo(ExecId(3333)))
                assertThat(correlation, nullValue())
                assertThat(inputs, equalTo(ExecInputs(TableValue(StringValue("hamal") to StringValue("justworks")))))
                assertThat(code, equalTo(CodeValue("code")))
            }
        }
    }

    @Autowired
    private lateinit var testInstance: InvokeAdhocHandler

    private val submittedInvocationReq = SubmittedInvokeAdhocReq(
        reqId = ReqId(1),
        status = ReqStatus.Submitted,
        id = ExecId(3333),
        inputs = InvocationInputs(TableValue(StringValue("hamal") to StringValue("justworks"))),
        code = CodeValue("code")
    )
}