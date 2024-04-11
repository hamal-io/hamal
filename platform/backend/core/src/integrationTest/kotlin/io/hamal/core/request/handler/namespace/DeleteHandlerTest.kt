package io.hamal.core.request.handler.namespace

import io.hamal.core.request.handler.BaseRequestHandlerTest
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.NamespaceAppendRequested
import io.hamal.lib.domain.request.NamespaceDeleteRequested
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.NamespaceCmdRepository
import io.hamal.repository.api.NamespaceQueryRepository.NamespaceQuery
import io.hamal.repository.api.NamespaceTreeCmdRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class DeleteHandlerTest : BaseRequestHandlerTest() {

    @BeforeEach
    fun beforeEach() {
        namespaceCmdRepository.clear()
        namespaceTreeRepository.clear()

        namespaceCmdRepository.create(
            NamespaceCmdRepository.CreateCmd(
                id = CmdId(2),
                namespaceId = NamespaceId.root,
                name = NamespaceName("root"),
                workspaceId = testWorkspace.id,
                features = NamespaceFeatures.default
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
    fun `Deletes namespace`() {
        testAppendHandler(requestedAppendNamespace)
        testDeleteHandler(requestedDeleteNamespace)

        val namespaces = namespaceQueryRepository.list(
            NamespaceQuery(
                limit = Limit.all,
                workspaceIds = listOf(testWorkspace.id)
            )
        )

        assertThat(namespaces, hasSize(1))
        assertThat(namespaceQueryRepository.find(NamespaceId(5)), Matchers.nullValue())
        with(namespaces[0]) {
            assertThat(namespaceId, equalTo(NamespaceId.root))
            assertThat(name, equalTo(NamespaceName("root")))
            assertThat(features, equalTo(NamespaceFeatures.default))
        }

        val tree = namespaceTreeRepository.find(NamespaceId.root)
        assertThat(tree!!.root.value, equalTo(NamespaceId.root))
        assertThat(tree.root.descendants, hasSize(0))
    }

    @Autowired
    private lateinit var testAppendHandler: NamespaceAppendHandler

    @Autowired
    private lateinit var testDeleteHandler: NamespaceDeleteHandler

    private val requestedDeleteNamespace by lazy {
        NamespaceDeleteRequested(
            requestId = RequestId(4),
            requestedBy = AuthId(4),
            requestStatus = RequestStatus.Submitted,
            id = NamespaceId(5),
            parentId = NamespaceId.root

        )
    }

    private val requestedAppendNamespace by lazy {
        NamespaceAppendRequested(
            requestId = RequestId(3),
            requestedBy = AuthId(4),
            requestStatus = RequestStatus.Submitted,
            parentId = NamespaceId.root,
            id = NamespaceId(5),
            workspaceId = testWorkspace.id,
            name = NamespaceName("awesome-namespace"),
            features = null
        )
    }
}