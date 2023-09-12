package io.hamal.core.req.handler.func

import io.hamal.core.req.handler.BaseReqHandlerTest
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.repository.api.FuncQueryRepository.FuncQuery
import io.hamal.repository.api.submitted_req.SubmittedCreateFuncReq
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
                reqId = ReqId(2),
                status = Submitted,
                id = FuncId(12345),
                groupId = testGroup.id,
                namespaceId = NamespaceId(23456),
                name = FuncName("another-func"),
                inputs = FuncInputs(),
                code = CodeType("")
            )
        )

        verifySingleFuncExists()
    }


    private fun verifySingleFuncExists() {
        funcQueryRepository.list(FuncQuery()).also { funcs ->
            assertThat(funcs, hasSize(1))
            with(funcs.first()) {
                assertThat(id, equalTo(FuncId(12345)))
                assertThat(name, equalTo(FuncName("awesome-func")))
                assertThat(inputs, equalTo(FuncInputs(MapType(mutableMapOf("hamal" to StringType("rocks"))))))
                assertThat(code, equalTo(CodeType("some code")))
            }
        }
    }

    @Autowired
    private lateinit var testInstance: CreateFuncHandler

    private val submitCreateFuncReq by lazy {
        SubmittedCreateFuncReq(
            reqId = ReqId(1),
            status = Submitted,
            id = FuncId(12345),
            groupId = testGroup.id,
            namespaceId = NamespaceId(23456),
            name = FuncName("awesome-func"),
            inputs = FuncInputs(MapType(mutableMapOf("hamal" to StringType("rocks")))),
            code = CodeType("some code")
        )
    }
}