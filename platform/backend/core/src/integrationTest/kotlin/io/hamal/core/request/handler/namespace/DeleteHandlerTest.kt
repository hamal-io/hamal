package io.hamal.core.request.handler.namespace

import io.hamal.core.request.handler.BaseRequestHandlerTest
import io.hamal.lib.common.domain.CmdId.Companion.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain._enum.RequestStatuses.Submitted
import io.hamal.lib.domain.request.NamespaceAppendRequested
import io.hamal.lib.domain.request.NamespaceDeleteRequested
import io.hamal.lib.domain.vo.AuthId.Companion.AuthId
import io.hamal.lib.domain.vo.NamespaceFeatures
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceId.Companion.NamespaceId
import io.hamal.lib.domain.vo.NamespaceName.Companion.NamespaceName
import io.hamal.lib.domain.vo.NamespaceTreeId
import io.hamal.lib.domain.vo.RequestId.Companion.RequestId
import io.hamal.lib.domain.vo.RequestStatus.Companion.RequestStatus
import io.hamal.repository.api.NamespaceCmdRepository
import io.hamal.repository.api.NamespaceQueryRepository
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
            NamespaceQueryRepository.NamespaceQuery(
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
    }

    @Autowired
    private lateinit var testAppendHandler: NamespaceAppendHandler

    @Autowired
    private lateinit var testDeleteHandler: NamespaceDeleteHandler

    private val requestedDeleteNamespace by lazy {
        NamespaceDeleteRequested(
            requestId = RequestId(4),
            requestedBy = AuthId(4),
            requestStatus = RequestStatus(Submitted),
            id = NamespaceId(5),
        )
    }

    private val requestedAppendNamespace by lazy {
        NamespaceAppendRequested(
            requestId = RequestId(3),
            requestedBy = AuthId(4),
            requestStatus = RequestStatus(Submitted),
            parentId = NamespaceId.root,
            id = NamespaceId(5),
            workspaceId = testWorkspace.id,
            name = NamespaceName("awesome-namespace"),
            features = null
        )
    }
}