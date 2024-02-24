package io.hamal.api.http.controller.topic

import io.hamal.core.adapter.namespace_tree.NamespaceTreeGetSubTreePort
import io.hamal.core.adapter.topic.TopicListPort
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain._enum.TopicType.*
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.sdk.api.ApiTopicList
import io.hamal.lib.sdk.api.ApiTopicList.Topic
import io.hamal.repository.api.TopicQueryRepository.TopicQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class TopicListController(
    private val topicList: TopicListPort,
    private val namespaceTreeGetSubTree: NamespaceTreeGetSubTreePort
) {

    @GetMapping("/v1/topics")
    fun list(
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: TopicId,
        @RequestParam(required = false, name = "names", defaultValue = "") topicNames: List<TopicName>,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit,
        @RequestParam(required = false, name = "ids", defaultValue = "") ids: List<TopicId>,
        @RequestParam(required = false, name = "workspace_ids", defaultValue = "") workspaceIds: List<WorkspaceId>,
        @RequestParam(required = false, name = "namespace_ids", defaultValue = "") namespaceIds: List<NamespaceId>
    ): ResponseEntity<ApiTopicList> {
        val allNamespaceIds = namespaceIds.flatMap { namespaceId ->
            namespaceTreeGetSubTree(namespaceId).values
        }
        return topicList(
            TopicQuery(
                afterId = afterId,
                names = topicNames,
                limit = limit,
                topicIds = ids,
                workspaceIds = workspaceIds,
                namespaceIds = allNamespaceIds,
                types = listOf(Namespace, Workspace, Public)
            )
        ).let { topics ->
            ResponseEntity.ok(
                ApiTopicList(
                    topics = topics.map { topic ->
                        Topic(
                            id = topic.id,
                            name = topic.name,
                            type = topic.type
                        )
                    }
                )
            )
        }
    }
}