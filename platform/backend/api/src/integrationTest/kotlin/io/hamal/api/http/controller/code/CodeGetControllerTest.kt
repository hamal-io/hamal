package io.hamal.api.http.controller.code

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode
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
    fun `Get code with version`() {
        codeCmdRepository.create(
            CodeCmdRepository.CreateCmd(
                id = CmdId(2),
                codeId = CodeId(2),
                groupId = testGroup.id,
                value = CodeValue("1 + 1")
            )
        )

        repeat(10) { iteration ->
            codeCmdRepository.update(
                CodeId(2), CodeCmdRepository.UpdateCmd(
                    CmdId(3 + iteration),
                    CodeValue("40 + ${2 + iteration}")
                )
            )
        }

        val r = getCode(CodeId(2), CodeVersion(4))

        with(r) {
            assertThat(id, equalTo(CodeId(2)))
            assertThat(value, equalTo(CodeValue("40 + 4")))
            assertThat(version, equalTo(CodeVersion(4)))
        }
    }

    @Test
    fun `Get code of latest version`() {
        codeCmdRepository.create(
            CodeCmdRepository.CreateCmd(
                id = CmdId(4),
                codeId = CodeId(3),
                groupId = testGroup.id,
                value = CodeValue("1 + 1")
            )
        )

        codeCmdRepository.update(
            CodeId(3), CodeCmdRepository.UpdateCmd(
                CmdId(5),
                CodeValue("40 + 2")
            )
        )

        with(getCode(CodeId(3))) {
            assertThat(id, equalTo(CodeId(3)))
            assertThat(value, equalTo(CodeValue("40 + 2")))
            assertThat(version, equalTo(CodeVersion(2)))
        }
    }

    @Test
    fun `Code with version does not exist`() {
        codeCmdRepository.create(
            CodeCmdRepository.CreateCmd(
                id = CmdId(6),
                codeId = CodeId(4),
                groupId = testGroup.id,
                value = CodeValue("1 + 1")
            )
        )

        val getCodeResponse = httpTemplate.get("/v1/code/{id}")
            .path("id", CodeId(4))
            .parameter("version", 2)
            .execute()

        assertThat(getCodeResponse.statusCode, equalTo(HttpStatusCode.NotFound))
        require(getCodeResponse is HttpErrorResponse) { "request was successful" }

        val error = getCodeResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Code not found"))

    }

    @Test
    fun `Code does not exist`() {
        val getCodeResponse = httpTemplate.get("/v1/code/33333333").execute()
        assertThat(getCodeResponse.statusCode, equalTo(HttpStatusCode.NotFound))
        require(getCodeResponse is HttpErrorResponse) { "request was successful" }

        val error = getCodeResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Code not found"))
    }
}