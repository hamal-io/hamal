package io.hamal.api.web.code

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.api.ApiCode
import io.hamal.lib.sdk.api.ApiCreateFuncReq
import io.hamal.lib.sdk.api.ApiError
import io.hamal.repository.api.CodeCmdRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test


internal class CodeGetControllerTest : CodeBaseControllerTest() {

    @Test
    fun `Get code by id`() {
        val createCode = codeCmdRepository.create(
            CodeCmdRepository.CreateCmd(
                id = CmdId(1),
                codeId = CodeId(1),
                groupId = testGroup.id,
                value = CodeValue("1 + 1")
            )
        )

        with(getCode(createCode.id)) {
            assertThat(id, equalTo(createCode.id))
            assertThat(value, equalTo(createCode.value))
            assertThat(version, equalTo(createCode.version))
        }
    }


    @Test
    fun `Get code latest without version`() {
        val createCode = codeCmdRepository.create(
            CodeCmdRepository.CreateCmd(
                id = CmdId(1),
                codeId = CodeId(1),
                groupId = testGroup.id,
                value = CodeValue("1 + 1")
            )
        )

        codeCmdRepository.update(
            CodeId(1), CodeCmdRepository.UpdateCmd(
                CmdId(2),
                CodeValue("40 + 2")
            )
        )


        val getCodeResponse = httpTemplate.get("/v1/code/1?codeVersion=2").execute()

        assertThat(getCodeResponse.statusCode, equalTo(HttpStatusCode.Ok))
        require(getCodeResponse is SuccessHttpResponse) { "request was not successful" }
        val resp = getCodeResponse.result(ApiCode::class)

        with(resp) {
            assertThat(id, equalTo(CodeId(1)))
            assertThat(value, equalTo(CodeValue("40 + 2")))
            assertThat(version, equalTo(CodeVersion(2)))
        }
    }

    @Test
    fun `Code does not exist`() {
        val getCodeResponse = httpTemplate.get("/v1/code/33333333").execute()
        assertThat(getCodeResponse.statusCode, equalTo(HttpStatusCode.NotFound))
        require(getCodeResponse is ErrorHttpResponse) { "request was successful" }

        val error = getCodeResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Code not found"))
    }


}