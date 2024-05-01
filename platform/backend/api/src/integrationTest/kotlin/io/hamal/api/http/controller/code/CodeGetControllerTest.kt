package io.hamal.api.http.controller.code

import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.domain.vo.CodeId.Companion.CodeId
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.domain.vo.CodeVersion.Companion.CodeVersion
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.sdk.api.ApiError
import io.hamal.repository.api.CodeCmdRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test


internal class CodeGetControllerTest : CodeBaseControllerTest() {

    @Test
    fun `Gets code by id`() {
        repeat(20) { iter ->
            val createCode = codeCmdRepository.create(
                CodeCmdRepository.CreateCmd(
                    id = CmdGen(),
                    codeId = CodeId(iter + 5),
                    workspaceId = testWorkspace.id,
                    value = ValueCode("1 + $iter")
                )
            )

            with(getCode(createCode.id)) {
                assertThat(id, equalTo(CodeId(iter + 5)))
                assertThat(value, equalTo(ValueCode("1 + $iter")))
                assertThat(version, equalTo(CodeVersion(1)))
            }
        }
    }


    @Test
    fun `Gets code with version`() {
        codeCmdRepository.create(
            CodeCmdRepository.CreateCmd(
                id = CmdGen(),
                codeId = CodeId(2),
                workspaceId = testWorkspace.id,
                value = ValueCode("1 + 1")
            )
        )

        repeat(10) { iter ->
            codeCmdRepository.update(
                CodeId(2), CodeCmdRepository.UpdateCmd(
                    CmdGen(),
                    ValueCode("40 + ${2 + iter}")
                )
            )
        }

        val r = getCode(CodeId(2), CodeVersion(4))

        with(r) {
            assertThat(id, equalTo(CodeId(2)))
            assertThat(value, equalTo(ValueCode("40 + 4")))
            assertThat(version, equalTo(CodeVersion(4)))
        }
    }

    @Test
    fun `Gets code of latest version`() {
        codeCmdRepository.create(
            CodeCmdRepository.CreateCmd(
                id = CmdGen(),
                codeId = CodeId(3),
                workspaceId = testWorkspace.id,
                value = ValueCode("1 + 1")
            )
        )

        repeat(5) {
            codeCmdRepository.update(
                CodeId(3), CodeCmdRepository.UpdateCmd(
                    CmdGen(),
                    ValueCode("40 + 2")
                )
            )
            assertThat(getCode(CodeId(3)).version, equalTo(CodeVersion(2)))
        }

        with(getCode(CodeId(3))) {
            assertThat(id, equalTo(CodeId(3)))
            assertThat(value, equalTo(ValueCode("40 + 2")))
        }
    }


    @Test
    fun `Tries to get code with version that does not exist`() {
        codeCmdRepository.create(
            CodeCmdRepository.CreateCmd(
                id = CmdGen(),
                codeId = CodeId(4),
                workspaceId = testWorkspace.id,
                value = ValueCode("1 + 1")
            )
        )

        val getCodeResponse = httpTemplate.get("/v1/codes/{id}")
            .path("id", CodeId(4))
            .parameter("version", 2)
            .execute()

        assertThat(getCodeResponse.statusCode, equalTo(HttpStatusCode.NotFound))
        require(getCodeResponse is HttpErrorResponse) { "request was successful" }

        val error = getCodeResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Code not found"))

    }

    @Test
    fun `Tries to get code that does not exist`() {
        val getCodeResponse = httpTemplate.get("/v1/codes/33333333").execute()
        assertThat(getCodeResponse.statusCode, equalTo(HttpStatusCode.NotFound))
        require(getCodeResponse is HttpErrorResponse) { "request was successful" }

        val error = getCodeResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Code not found"))
    }
}