package io.hamal.backend

import io.hamal.backend.repository.api.ReqQueryRepository
import io.hamal.backend.service.query.EventQueryService
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.req.*
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
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
class TenantEventIT(
    @LocalServerPort val localPort: Int,
    @Autowired val reqQueryRepository: ReqQueryRepository,
    @Autowired val eventQueryService: EventQueryService<*>
) {
    @Test
    fun `Create topic`() {
        val response = request(
            CreateTopicReq(name = TopicName("eth::block_processed"))
        )

        assertThat(response.statusCode, equalTo(HttpStatusCode.Accepted))
        require(response is SuccessHttpResponse) { "request was not successful" }
        val result = response.result(SubmittedCreateTopicReq::class)

        assertThat(result.name, equalTo(TopicName("eth::block_processed")))

        verifyReqCompleted(result.id)
        verifyTopicCreated(result.topicId)
    }

    @PostConstruct
    fun setup() {
        httpTemplate = HttpTemplate("http://localhost:${localPort}")
    }

    private fun request(req: CreateTopicReq) =
        httpTemplate
            .post("/v1/topics")
            .body(req)
            .execute()


    private fun verifyReqCompleted(id: ReqId) {
        with(reqQueryRepository.find(id)!!) {
            assertThat(id, equalTo(id))
            assertThat(status, equalTo(ReqStatus.Completed))
        }
    }

    private fun verifyTopicCreated(topicId: TopicId) {
        with(eventQueryService.findTopic(topicId)!!) {
            assertThat(id, equalTo(topicId))
            assertThat(name, equalTo(TopicName("eth::block_processed")))
        }
    }

    private lateinit var httpTemplate: HttpTemplate
}