package io.hamal.core.req.handler.endpoint

import io.hamal.core.req.handler.BaseReqHandlerTest
import io.hamal.lib.domain._enum.EndpointMethod
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.vo.EndpointId
import io.hamal.lib.domain.vo.EndpointName
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.ReqId
import io.hamal.repository.api.EndpointQueryRepository.EndpointQuery
import io.hamal.repository.api.submitted_req.EndpointCreateSubmitted
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class EndpointCreateHandlerTest : BaseReqHandlerTest() {

    @Test
    fun `Creates endpoint`() {
        testInstance(submitCreateEndpointReq)

        endpointRepository.list(EndpointQuery(groupIds = listOf())).also { endpoints ->
            assertThat(endpoints, hasSize(1))
            with(endpoints.first()) {
                assertThat(id, equalTo(EndpointId(12345)))
                assertThat(name, equalTo(EndpointName("awesome-endpoint")))
                assertThat(groupId, equalTo(testGroup.id))
                assertThat(funcId, equalTo(FuncId(23456)))
                assertThat(method, equalTo(EndpointMethod.Post))
            }
        }
    }

    @Autowired
    private lateinit var testInstance: EndpointCreateHandler

    private val submitCreateEndpointReq by lazy {
        EndpointCreateSubmitted(
            id = ReqId(1),
            status = Submitted,
            endpointId = EndpointId(12345),
            groupId = testGroup.id,
            funcId = FuncId(23456),
            name = EndpointName("awesome-endpoint"),
            method = EndpointMethod.Post
        )
    }
}