package io.hamal.backend.web.event

import io.hamal.backend.BackendConfig
import io.hamal.backend.repository.api.ReqQueryRepository
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.backend.service.query.EventQueryService
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.req.CreateTopicReq
import io.hamal.lib.domain.req.ReqStatus
import io.hamal.lib.domain.req.SubmittedCreateTopicReq
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.domain.ListTopicsResponse
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
internal sealed class BaseEventIT(
    val localPort: Int,
    val reqQueryRepository: ReqQueryRepository,
    val eventQueryService: EventQueryService<*>,
    val eventBrokerRepository: LogBrokerRepository<*>
) {

    @BeforeEach
    fun before() {
        eventBrokerRepository.clear()
    }

    fun listTopics(): ListTopicsResponse {
        val listTopicsResponse = httpTemplate
            .get("/v1/topics")
            .execute()
        assertThat(listTopicsResponse.statusCode, equalTo(HttpStatusCode.Ok))
        require(listTopicsResponse is SuccessHttpResponse) { "request was not successful" }
        return listTopicsResponse.result(ListTopicsResponse::class)
    }

    fun createTopic(topicName: TopicName): SubmittedCreateTopicReq {
        val createTopicResponse = httpTemplate
            .post("/v1/topics")
            .body(CreateTopicReq(topicName))
            .execute()

        assertThat(createTopicResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(createTopicResponse is SuccessHttpResponse) { "request was not successful" }

        return createTopicResponse.result(SubmittedCreateTopicReq::class)
    }


    fun verifyReqCompleted(id: ReqId) {
        with(reqQueryRepository.find(id)!!) {
            assertThat(id, equalTo(id))
            assertThat(status, equalTo(ReqStatus.Completed))
        }
    }

    internal val httpTemplate = HttpTemplate("http://localhost:${localPort}")
}