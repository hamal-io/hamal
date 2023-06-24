package io.hamal.backend.instance.web.event

import io.hamal.backend.instance.service.query.EventQueryService
import io.hamal.backend.repository.api.ReqQueryRepository
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.lib.domain.vo.Content
import io.hamal.lib.domain.vo.ContentType
import io.hamal.lib.domain.vo.TopicName
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort

internal class AppendEventRouteIT(
    @LocalServerPort localPort: Int,
    @Autowired reqQueryRepository: ReqQueryRepository,
    @Autowired eventQueryService: EventQueryService<*>,
    @Autowired eventBrokerRepository: LogBrokerRepository<*>
) : BaseEventRouteIT(
    localPort = localPort,
    reqQueryRepository = reqQueryRepository,
    eventQueryService = eventQueryService,
    eventBrokerRepository = eventBrokerRepository
) {
    @Test
    fun `Append event`() {
        val topicResponse = createTopic(TopicName("namespace::topics_one"))
        val result = appendEvent(
            topicResponse.topicId,
            ContentType("application/json"),
            Content("""{"hamal":"rocks"}""")
        )

        Thread.sleep(100)
        verifyReqCompleted(result.id)

        with(listEvents(topicResponse.topicId)) {
            assertThat(events, hasSize(1))

            val event = events.first()
            assertThat(event.contentType, equalTo(ContentType("application/json")))
            assertThat(event.content, equalTo(Content("""{"hamal":"rocks"}""")))
        }
    }
}