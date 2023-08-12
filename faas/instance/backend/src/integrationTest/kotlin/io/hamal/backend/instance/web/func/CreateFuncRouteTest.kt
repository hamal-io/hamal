package io.hamal.backend.instance.web.func

import io.hamal.lib.domain.req.CreateFuncReq
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.kua.type.TableType
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class CreateFuncRouteTest : BaseFuncRouteTest() {
    @Test
    fun `Create func`() {
        val result = createFunc(
            CreateFuncReq(
                name = FuncName("test-func"),
                inputs = FuncInputs(TableType(StringType("hamal") to StringType("rocks"))),
                code = CodeType("13 + 37")
            )
        )
        awaitCompleted(result.reqId)
        verifyFuncCreated(result.id(::FuncId))
    }
}

private fun CreateFuncRouteTest.verifyFuncCreated(funcId: FuncId) {
    with(funcQueryRepository.get(funcId)) {
        assertThat(id, equalTo(funcId))
        assertThat(name, equalTo(FuncName("test-func")))
        assertThat(inputs, equalTo(FuncInputs(TableType(StringType("hamal") to StringType("rocks")))))
        assertThat(code, equalTo(CodeType("13 + 37")))
    }
}