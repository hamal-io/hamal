package io.hamal.core.request.handler.namespace

import io.hamal.core.request.handler.BaseReqHandlerTest
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain.request.NamespaceCreateRequested
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.domain.vo.RequestId
import io.hamal.repository.api.NamespaceQueryRepository.NamespaceQuery
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired


internal class NamespaceCreateHandlerTest : BaseReqHandlerTest() {

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
            NamespaceCreateRequested(
                id = RequestId(1),
                status = Submitted,
                namespaceId = NamespaceId(12345),
                groupId = testGroup.id,
                name = NamespaceName("another-namespace")
            )
        )

        verifySingleNamespaceExists()
    }

    @Test
    fun `Creates namespace with type`() {
        testInstance(
            NamespaceCreateRequested(
                id = RequestId(1),
                status = Submitted,
                namespaceId = NamespaceId(12345),
                groupId = testGroup.id,
                name = NamespaceName("awesome-namespace")
            )
        )

        with(namespaceQueryRepository.get(NamespaceId(12345))) {
            assertThat(id, equalTo(NamespaceId(12345)))
            assertThat(name, equalTo(NamespaceName("awesome-namespace")))
        }

        assertThat(namespaceQueryRepository.list(NamespaceQuery(groupIds = listOf())), hasSize(1))

    }


    private fun verifySingleNamespaceExists() {
        namespaceQueryRepository.list(NamespaceQuery(groupIds = listOf())).also { namespaces ->
            assertThat(namespaces, hasSize(1))
            with(namespaces.first()) {
                assertThat(id, equalTo(NamespaceId(12345)))
                assertThat(name, equalTo(NamespaceName("awesome-namespace")))
            }
        }
    }

    @Autowired
    private lateinit var testInstance: NamespaceCreateHandler

    private val submitCreateNamespaceReq by lazy {
        NamespaceCreateRequested(
            id = RequestId(1),
            status = Submitted,
            namespaceId = NamespaceId(12345),
            groupId = testGroup.id,
            name = NamespaceName("awesome-namespace")
        )
    }
}