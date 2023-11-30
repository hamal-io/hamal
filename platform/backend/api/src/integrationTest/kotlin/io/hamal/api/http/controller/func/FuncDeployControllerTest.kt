package io.hamal.api.http.controller.func

import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiFuncCreateReq
import io.hamal.lib.sdk.api.ApiFuncCreateSubmitted
import io.hamal.lib.sdk.api.ApiFuncUpdateReq
import io.hamal.repository.api.Func
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class FuncDeployControllerTest : FuncBaseControllerTest() {

    @Nested
    inner class DeployVersionTest {

        @Test
        fun `Deploys higher version`() {
            val func = setup()
            repeat(20) { iter ->
                awaitCompleted(
                    deployVersion(func.id, CodeVersion(iter + 1))
                )
                assertThat(funcQueryRepository.get(func.id).code.deployedVersion, equalTo(CodeVersion(iter + 1)))
            }

            with(funcQueryRepository.get(func.id)) {
                assertThat(name, equalTo(FuncName("test-func")))
                assertThat(code.version, equalTo(CodeVersion(20)))
                assertThat(codeQueryRepository.get(code.id, code.deployedVersion).value, equalTo(CodeValue("code-19")))
            }

        }

        @Test
        fun `Deploys same version`() {
            val func = setup()

            repeat(20) {
                awaitCompleted(
                    deployVersion(func.id, CodeVersion(10))
                )
                assertThat(funcQueryRepository.get(func.id).code.deployedVersion, equalTo(CodeVersion(10)))
            }

            with(funcQueryRepository.get(func.id)) {
                assertThat(name, equalTo(FuncName("test-func")))
                assertThat(code.version, equalTo(CodeVersion(20)))
                assertThat(codeQueryRepository.get(code.id, code.deployedVersion).value, equalTo(CodeValue("code-9")))
            }
        }

        @Test
        fun `Deploys prior version`() {
            val func = setup()

            repeat(20) { iter ->
                awaitCompleted(
                    deployVersion(func.id, CodeVersion(20 - iter))
                )
                assertThat(funcQueryRepository.get(func.id).code.deployedVersion, equalTo(CodeVersion(20 - iter)))
            }

            with(funcQueryRepository.get(func.id)) {
                assertThat(name, equalTo(FuncName("test-func")))
                assertThat(code.version, equalTo(CodeVersion(20)))
                assertThat(codeQueryRepository.get(code.id, code.deployedVersion).value, equalTo(CodeValue("13 + 37")))
            }
        }

        @Test
        fun `Tries to deploy to func that does not exist`() {
            val res = httpTemplate.post("/v1/funcs/1234/deploy/25").execute()

            assertThat(res.statusCode, equalTo(HttpStatusCode.NotFound))
            require(res is HttpErrorResponse) { "request was successful" }

            val error = res.error(ApiError::class)
            assertThat(error.message, equalTo("Func not found"))
        }

        @Test
        fun `Tries to deploy version that does not exists`() {
            val f: ApiFuncCreateSubmitted = createFunc(
                ApiFuncCreateReq(
                    name = FuncName("Func-base"), inputs = FuncInputs(), code = CodeValue("40 + 2")
                )
            )

            val res = httpTemplate.post("/v1/funcs/{funcId}/deploy/25").path("funcId", f.funcId).execute()

            assertThat(res.statusCode, equalTo(HttpStatusCode.NotFound))
            require(res is HttpErrorResponse) { "request was successful" }

            val error = res.error(ApiError::class)
            assertThat(error.message, equalTo("Code not found"))
        }

        @Nested
        inner class DeployLatestTest {

            @Test
            fun `Deploys latest version`() {
                val funcId = setup().id
                awaitCompleted(deployLatestVersion(funcId))

                with(funcQueryRepository.get(funcId))
                {
                    assertThat(name, equalTo(FuncName("test-func")))
                    assertThat(
                        codeQueryRepository.get(code.id, code.deployedVersion).value, equalTo(CodeValue("code-19"))
                    )
                    assertThat(code.deployedVersion, equalTo(code.version))
                }
            }

            @Test
            fun `Deploys latest version with message`() {
                val funcId = setup().id
                awaitCompleted(
                    deployLatestVersionWithMessage(funcId, DeployMessage("SuperFunc"))
                )
                with(funcQueryRepository.get(funcId))
                {
                    assertThat(name, equalTo(FuncName("test-func")))
                    assertThat(
                        codeQueryRepository.get(code.id, code.deployedVersion).value, equalTo(CodeValue("code-19"))
                    )
                    assertThat(deployMessage, equalTo(DeployMessage("SuperFunc")))
                }
            }
        }

        private fun setup(): Func {
            val func = awaitCompleted(
                createFunc(
                    ApiFuncCreateReq(
                        name = FuncName("test-func"), inputs = FuncInputs(), code = CodeValue("13 + 37")
                    )
                )
            )

            for (i in 1..19) {
                awaitCompleted(
                    updateFunc(
                        func.funcId, ApiFuncUpdateReq(
                            name = null, inputs = null, code = CodeValue("code-${i}")
                        )
                    )
                )
            }

            return funcQueryRepository.get(func.funcId)
        }
    }
}