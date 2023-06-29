package io.hamal.backend.instance.req.handler.func

import io.hamal.backend.instance.req.handler.BaseReqHandlerTest
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.req.ReqStatus
import io.hamal.lib.domain.req.SubmittedCreateFuncReq
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.script.api.value.StringValue
import io.hamal.lib.script.api.value.TableValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class CreateFuncHandlerTest : BaseReqHandlerTest() {

    @Test
    fun `Creates func`() {
        testInstance(submitCreateFuncReq)

        verifySingleFuncExists()
    }

    @Test
    fun `Func with id already exists`() {
        testInstance(submitCreateFuncReq)

        testInstance(
            SubmittedCreateFuncReq(
                id = ReqId(2),
                status = ReqStatus.Submitted,
                funcId = FuncId(12345),
                funcName = FuncName("another-func"),
                inputs = FuncInputs(),
                code = Code("")
            )
        )

        verifySingleFuncExists()
    }


    private fun verifySingleFuncExists() {
        funcQueryRepository.list { }.also { funcs ->
            assertThat(funcs, hasSize(1))
            with(funcs.first()) {
                assertThat(id, equalTo(FuncId(12345)))
                assertThat(name, equalTo(FuncName("awesome-func")))
                assertThat(inputs, equalTo(FuncInputs(TableValue(StringValue("hamal") to StringValue("rocks")))))
                assertThat(code, equalTo(Code("some code")))
            }
        }
    }

    @Autowired
    private lateinit var testInstance: CreateFuncHandler

    private val submitCreateFuncReq = SubmittedCreateFuncReq(
        id = ReqId(1),
        status = ReqStatus.Submitted,
        funcId = FuncId(12345),
        funcName = FuncName("awesome-func"),
        inputs = FuncInputs(TableValue(StringValue("hamal") to StringValue("rocks"))),
        code = Code("some code")
    )
}