package io.hamal.core.adapter

import io.hamal.lib.domain.vo.GroupId
import io.hamal.repository.api.Group
import io.hamal.repository.api.GroupQueryRepository
import io.hamal.repository.api.GroupQueryRepository.GroupQuery
import org.springframework.stereotype.Component


interface GetGroupPort {
    operator fun <T : Any> invoke(groupId: GroupId, responseHandler: (Group) -> T): T
}

interface ListGroupsPort {
    operator fun <T : Any> invoke(query: GroupQuery, responseHandler: (List<Group>) -> T): T
}

interface GroupPort : GetGroupPort, ListGroupsPort

@Component
class GroupAdapter(
    private val groupQueryRepository: GroupQueryRepository
) : GroupPort {
    
    override operator fun <T : Any> invoke(
        groupId: GroupId,
        responseHandler: (Group) -> T
    ): T = responseHandler(groupQueryRepository.get(groupId))

    override operator fun <T : Any> invoke(
        query: GroupQuery,
        responseHandler: (List<Group>) -> T
    ): T = responseHandler(groupQueryRepository.list(query))
}