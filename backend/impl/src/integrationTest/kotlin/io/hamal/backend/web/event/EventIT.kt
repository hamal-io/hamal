//package io.hamal.backend.web.event
//
//import io.hamal.backend.*
//import io.hamal.backend.repository.api.ReqQueryRepository
//import io.hamal.backend.service.query.EventQueryService
//import io.hamal.lib.domain.ReqId
//import io.hamal.lib.domain.req.CreateTopicReq
//import io.hamal.lib.domain.req.ReqStatus
//import io.hamal.lib.domain.req.SubmittedCreateTopicReq
//import io.hamal.lib.domain.vo.TopicId
//import io.hamal.lib.domain.vo.TopicName
//import io.hamal.lib.http.HttpStatusCode
//import io.hamal.lib.http.HttpTemplate
//import io.hamal.lib.http.SuccessHttpResponse
//import io.hamal.lib.http.body
//import io.hamal.lib.sdk.domain.ListTopicsResponse
//import jakarta.annotation.PostConstruct
//import org.hamcrest.MatcherAssert
//import org.hamcrest.Matchers
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.extension.ExtendWith
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.boot.test.web.server.LocalServerPort
//import org.springframework.test.annotation.DirtiesContext
//import org.springframework.test.context.ActiveProfiles
//import org.springframework.test.context.ContextConfiguration
//import org.springframework.test.context.junit.jupiter.SpringExtension
//
//@ExtendWith(SpringExtension::class)
//@ContextConfiguration(
//    classes = [
//        BackendConfig::class,
//    ]
//)
//@SpringBootTest(
//    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
//)
//@ActiveProfiles("memory")
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
//class EventIT(
//    @LocalServerPort val localPort: Int,
//    @Autowired val reqQueryRepository: ReqQueryRepository,
//    @Autowired val eventQueryService: EventQueryService<*>
//) {
//    @Test
//    fun `No event topics created yet`() {
//        val result = listTopics()
//        MatcherAssert.assertThat(result.topics, Matchers.empty())
//    }
//
//    @Test
//    fun `Creates event topic`() {
//        val result = createTopic(TopicName("namespace::topics_one"))
//        MatcherAssert.assertThat(result.name, Matchers.equalTo(TopicName("namespace::topics_one")))
//
//        Thread.sleep(500)
//
//        verifyReqCompleted(result.id)
//        verifyTopicCreated(result.topicId)
//
//        with(listTopics()) {
//            org.hamcrest.MatcherAssert.assertThat(topics, org.hamcrest.Matchers.hasSize(1))
//            org.hamcrest.MatcherAssert.assertThat(
//                topics.first().name,
//                org.hamcrest.Matchers.equalTo(TopicName("namespace::topics_one"))
//            )
//        }
//    }
//
//    @PostConstruct
//    fun setup() {
//        httpTemplate = HttpTemplate("http://localhost:${localPort}")
//    }
//
//
//    internal lateinit var httpTemplate: HttpTemplate
//}
//
//private fun EventIT.listTopics(): ListTopicsResponse {
//    val listTopicsResponse = httpTemplate
//        .get("/v1/topics")
//        .execute()
//
//    MatcherAssert.assertThat(listTopicsResponse.statusCode, Matchers.equalTo(HttpStatusCode.Ok))
//    require(listTopicsResponse is SuccessHttpResponse) { "request was not successful" }
//
//    return listTopicsResponse.result(ListTopicsResponse::class)
//}
//
//private fun EventIT.createTopic(topicName: TopicName): SubmittedCreateTopicReq {
//    val createTopicResponse = httpTemplate
//        .post("/v1/topics")
//        .body(CreateTopicReq(topicName))
//        .execute()
//
//    MatcherAssert.assertThat(createTopicResponse.statusCode, Matchers.equalTo(HttpStatusCode.Accepted))
//    require(createTopicResponse is SuccessHttpResponse) { "request was not successful" }
//
//    return createTopicResponse.result(SubmittedCreateTopicReq::class)
//}
//
//private fun EventIT.verifyReqCompleted(id: ReqId) {
//    with(reqQueryRepository.find(id)!!) {
//        MatcherAssert.assertThat(id, Matchers.equalTo(id))
//        MatcherAssert.assertThat(status, Matchers.equalTo(ReqStatus.Completed))
//    }
//}
//
//private fun EventIT.verifyTopicCreated(topicId: TopicId) {
//    val t = eventQueryService.queryTopics { }
//    with(eventQueryService.findTopic(topicId)!!) {
//        MatcherAssert.assertThat(id, Matchers.equalTo(topicId))
//        MatcherAssert.assertThat(name, Matchers.equalTo(TopicName("namespace::topics_one")))
//    }
//}
