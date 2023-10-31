package io.hamal.core.req.handler.namespace

import io.hamal.core.req.handler.BaseReqHandlerTest
import io.hamal.lib.domain.vo.ReqId
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceInputs
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.repository.api.NamespaceQueryRepository.NamespaceQuery
import io.hamal.repository.api.submitted_req.NamespaceCreateSubmitted
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
            NamespaceCreateSubmitted(
                id = ReqId(1),
                status = Submitted,
                namespaceId = NamespaceId(12345),
                groupId = testGroup.id,
                name = NamespaceName("another-namespace"),
                inputs = NamespaceInputs()
            )
        )

        verifySingleNamespaceExists()
    }


    private fun verifySingleNamespaceExists() {
        namespaceQueryRepository.list(NamespaceQuery(groupIds = listOf())).also { namespaces ->
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

    private val submitCreateNamespaceReq by lazy {
        NamespaceCreateSubmitted(
            id = ReqId(1),
            status = Submitted,
            namespaceId = NamespaceId(12345),
            groupId = testGroup.id,
            name = NamespaceName("awesome-namespace"),
            inputs = NamespaceInputs(
                MapType(mutableMapOf("hamal" to StringType("rocks")))
            )
        )
    }
}