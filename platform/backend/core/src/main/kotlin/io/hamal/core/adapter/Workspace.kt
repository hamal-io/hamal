package io.hamal.core.adapter

import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.repository.api.Group
import io.hamal.repository.api.GroupQueryRepository
import io.hamal.repository.api.GroupQueryRepository.GroupQuery
import org.springframework.stereotype.Component


interface GroupGetPort {
    operator fun <T : Any> invoke(groupId: GroupId, responseHandler: (Group) -> T): T
}

interface GroupListPort {
    operator fun <T : Any> invoke(query: GroupQuery, responseHandler: (List<Group>) -> T): T
    operator fun <T : Any> invoke(accountId: AccountId, responseHandler: (List<Group>) -> T): T
}

interface GroupPort : GroupGetPort, GroupListPort

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

    override fun <T : Any> invoke(accountId: AccountId, responseHandler: (List<Group>) -> T): T {
        // FIXME this must come directly from the repository
        return responseHandler(groupQueryRepository.list(
            GroupQuery(
                limit = Limit.all
            )
        ).filter { it.creatorId == accountId }
        )
    }
}