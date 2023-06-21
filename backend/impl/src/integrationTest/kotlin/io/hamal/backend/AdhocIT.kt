package io.hamal.backend

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
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.server.LocalServerPort
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
    webEnvironment = WebEnvironment.RANDOM_PORT,
)
@ActiveProfiles("memory")
class HamalIT(
    @LocalServerPort val localPort: Int
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
    }

    @PostConstruct
    fun setup() {
        sdk = DefaultHamalSdk("http://localhost:${localPort}")
    }

    private lateinit var sdk: HamalSdk
}