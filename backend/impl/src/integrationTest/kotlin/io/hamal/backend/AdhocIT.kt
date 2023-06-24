package io.hamal.backend

import io.hamal.backend.repository.api.ExecQueryRepository
import io.hamal.backend.repository.api.ReqQueryRepository
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.req.InvokeAdhocReq
import io.hamal.lib.domain.req.ReqStatus
import io.hamal.lib.domain.req.SubmittedInvokeAdhocReq
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import jakarta.annotation.PostConstruct
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension


@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        BackendConfig::class,
    ]
)
@SpringBootTest(
    webEnvironment = RANDOM_PORT,
)
@ActiveProfiles("memory")
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class AdhocIT(
    @LocalServerPort val localPort: Int,
    @Autowired val execQueryRepository: ExecQueryRepository,
    @Autowired val reqQueryRepository: ReqQueryRepository
) {
    @Test
    fun `Submits adhoc requests without inputs or secrets`() {
        val response = request(
            InvokeAdhocReq(
                inputs = InvocationInputs(),
                secrets = InvocationSecrets(),
                code = Code("40 + 2")
            )
        )

        assertThat(response.statusCode, equalTo(HttpStatusCode.Accepted))
        require(response is SuccessHttpResponse) { "request was not successful" }
        val result = response.result(SubmittedInvokeAdhocReq::class)

        assertThat(result.status, equalTo(ReqStatus.Submitted))
        assertThat(result.inputs, equalTo(InvocationInputs()))
        assertThat(result.secrets, equalTo(InvocationSecrets()))
        assertThat(result.code, equalTo(Code("40 + 2")))

        Thread.sleep(500)

        verifyReqCompleted(result.id)
        verifyExecQueued(result.execId)
    }

    @PostConstruct
    fun setup() {
        httpTemplate = HttpTemplate("http://localhost:${localPort}")
    }

    private fun request(req: InvokeAdhocReq) =
        httpTemplate
            .post("/v1/adhoc")
            .body(req)
            .execute()


    private fun verifyReqCompleted(id: ReqId) {
        with(reqQueryRepository.find(id)!!) {
            assertThat(id, equalTo(id))
            assertThat(status, equalTo(ReqStatus.Completed))
        }
    }

    private fun verifyExecQueued(execId: ExecId) {
        with(execQueryRepository.find(execId)!!) {
            assertThat(id, equalTo(execId))
            assertThat(status, equalTo(ExecStatus.Queued))

            assertThat(correlation, nullValue())
            assertThat(inputs, equalTo(ExecInputs()))
            assertThat(secrets, equalTo(ExecSecrets()))
            assertThat(code, equalTo(Code("40 + 2")))

        }
    }

    private lateinit var httpTemplate: HttpTemplate
}