package io.hamal.backend.instance.web

import io.hamal.backend.instance.BackendConfig
import io.hamal.backend.repository.api.ReqQueryRepository
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.req.ReqStatus
import io.hamal.lib.http.HttpTemplate
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
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
    webEnvironment = RANDOM_PORT
)
@ActiveProfiles("memory")
abstract class BaseRouteIT(
    val localPort: Int,
    val reqQueryRepository: ReqQueryRepository,
    val eventBrokerRepository: LogBrokerRepository<*>
) {

    @BeforeEach
    fun before() {
        eventBrokerRepository.clear()
    }

    fun verifyReqFailed(id: ReqId) {
        with(reqQueryRepository.find(id)!!) {
            assertThat(id, equalTo(id))
            assertThat(status, equalTo(ReqStatus.Failed))
        }
    }

    fun verifyReqCompleted(id: ReqId) {
        with(reqQueryRepository.find(id)!!) {
            assertThat(id, equalTo(id))
            assertThat(status, equalTo(ReqStatus.Completed))
        }
    }

    val httpTemplate = HttpTemplate("http://localhost:${localPort}")
}