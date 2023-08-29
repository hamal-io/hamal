package io.hamal.backend.instance.req.handler.namespace

import io.hamal.backend.instance.req.handler.BaseReqHandlerTest
import io.hamal.repository.api.submitted_req.SubmittedCreateNamespaceReq
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceInputs
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class CreateNamespaceHandlerTest : BaseReqHandlerTest() {

    @BeforeEach
    fun beforeEach() {
        namespaceCmdRepository.clear()
    }

    @Test
    fun `Creates namespace`() {
        testInstance(submitCreateNamespaceReq)

        verifySingleNamespaceExists()
    }

    @Test
    fun `Namespace with id already exists`() {
        testInstance(submitCreateNamespaceReq)

        testInstance(
            SubmittedCreateNamespaceReq(
                reqId = ReqId(2),
                status = Submitted,
                id = NamespaceId(12345),
                name = NamespaceName("another-namespace"),
                inputs = NamespaceInputs()
            )
        )

        verifySingleNamespaceExists()
    }


    private fun verifySingleNamespaceExists() {
        namespaceQueryRepository.list { }.also { namespaces ->
            assertThat(namespaces, hasSize(1))
            with(namespaces.first()) {
                assertThat(id, equalTo(NamespaceId(12345)))
                assertThat(name, equalTo(NamespaceName("awesome-namespace")))
                assertThat(inputs, equalTo(NamespaceInputs(MapType(mutableMapOf("hamal" to StringType("rocks"))))))
            }
        }
    }

    @Autowired
    private lateinit var testInstance: CreateNamespaceHandler

    private val submitCreateNamespaceReq = SubmittedCreateNamespaceReq(
        reqId = ReqId(1),
        status = Submitted,
        id = NamespaceId(12345),
        name = NamespaceName("awesome-namespace"),
        inputs = NamespaceInputs(
            MapType(mutableMapOf("hamal" to StringType("rocks")))
        )
    )
}