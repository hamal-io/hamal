package io.hamal.core.request.handler.namespace

import io.hamal.core.request.handler.BaseRequestHandlerTest
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain.request.NamespaceAppendRequested
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.NamespaceCmdRepository
import io.hamal.repository.api.NamespaceQueryRepository.NamespaceQuery
import io.hamal.repository.api.NamespaceTreeCmdRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired


internal class NamespaceAppendHandlerTest : BaseRequestHandlerTest() {

    @BeforeEach
    fun beforeEach() {
        namespaceCmdRepository.clear()
        namespaceTreeRepository.clear()

        namespaceCmdRepository.create(
            NamespaceCmdRepository.CreateCmd(
                id = CmdId(2),
                namespaceId = NamespaceId.root,
                name = NamespaceName("root"),
                workspaceId = testWorkspace.id
            )
        )
        namespaceTreeRepository.create(
            NamespaceTreeCmdRepository.CreateCmd(
                id = CmdId(2),
                treeId = NamespaceTreeId.root,
                rootNodeId = NamespaceId.root,
                workspaceId = testWorkspace.id
            )
        )
    }

    @Test
    fun `Appends namespace`() {
        testInstance(requestedAppendNamespace)
        namespaceQueryRepository.list(
            NamespaceQuery(
                limit = Limit.all,
                workspaceIds = listOf(testWorkspace.id)
            )
        ).also { namespaces ->
            assertThat(namespaces, hasSize(2))

            with(namespaces[0]) {
                assertThat(id, equalTo(NamespaceId(12345)))
                assertThat(name, equalTo(NamespaceName("awesome-namespace")))
            }

            with(namespaces[1]) {
                assertThat(id, equalTo(NamespaceId.root))
                assertThat(name, equalTo(NamespaceName("root")))
            }
        }
    }

    @Autowired
    private lateinit var testInstance: NamespaceAppendHandler

    private val requestedAppendNamespace by lazy {
        NamespaceAppendRequested(
            requestId = RequestId(3),
            requestedBy = AuthId(4),
            requestStatus = Submitted,
            parentId = NamespaceId.root,
            id = NamespaceId(12345),
            workspaceId = testWorkspace.id,
            name = NamespaceName("awesome-namespace")
        )
    }
}