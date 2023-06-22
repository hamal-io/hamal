package io.hamal.backend

import io.hamal.backend.repository.api.ReqQueryRepository
import io.hamal.lib.common.Shard
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.req.InvokeAdhocReq
import io.hamal.lib.domain.req.ReqStatus
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.InvocationSecrets
import io.hamal.lib.sdk.DefaultHamalSdk
import io.hamal.lib.sdk.HamalSdk
import jakarta.annotation.PostConstruct
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.equalTo
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
class HamalIT(
    @LocalServerPort val localPort: Int,
    @Autowired val reqQueryRepository: ReqQueryRepository
) {

    @Test
    fun `Submits adhoc requests without inputs or secrets`() {
        val result = sdk.adhocService().submit(
            InvokeAdhocReq(
                inputs = InvocationInputs(),
                secrets = InvocationSecrets(),
                code = Code("40 + 2")
            )
        )

        assertThat(result.status, equalTo(ReqStatus.Submitted))
        assertThat(result.inputs, equalTo(InvocationInputs()))
        assertThat(result.secrets, equalTo(InvocationSecrets()))
        assertThat(result.code, equalTo(Code("40 + 2")))

        verifyReqCompleted(result.id)
    }

    @PostConstruct
    fun setup() {
        sdk = DefaultHamalSdk("http://localhost:${localPort}")
    }

    private fun verifyReqCompleted(id: ReqId) {
        with(reqQueryRepository.find(id)!!) {
            assertThat(id, equalTo(id))
            assertThat(status, equalTo(ReqStatus.Completed))
            assertThat(shard, equalTo(Shard(1)))
        }
    }

    private lateinit var sdk: HamalSdk
}