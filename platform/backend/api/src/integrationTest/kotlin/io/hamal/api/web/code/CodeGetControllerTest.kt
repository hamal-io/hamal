package io.hamal.api.web.code

import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.api.ApiCreateFuncReq
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test


internal class CodeGetControllerTest : CodeBaseControllerTest() {

    @Test
    fun `Get code by id`() {
        val funcId: FuncId = awaitCompleted(
            createCode(
                ApiCreateFuncReq(
                    name = FuncName("func-one"),
                    namespaceId = null,
                    inputs = FuncInputs(MapType(mutableMapOf("hamal" to StringType("rockz")))),
                    code = CodeValue("1+1")
                )
            )
        ).id(::FuncId)

        val code = getFunc(funcId).code
        val codeRes = getCode(code.id)

        with(codeRes) {
            assertThat(id, equalTo(code.id))
            assertThat(value, equalTo(code.value))
            assertThat(version, equalTo(code.version))
        }
    }


}