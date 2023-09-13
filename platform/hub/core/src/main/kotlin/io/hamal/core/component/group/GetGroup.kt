package io.hamal.core.component.group

import io.hamal.lib.domain.vo.GroupId
import io.hamal.repository.api.Group
import io.hamal.repository.api.GroupQueryRepository
import org.springframework.stereotype.Component

@Component
class GetGroup(private val groupQueryRepository: GroupQueryRepository) {
    operator fun <T : Any> invoke(
        groupId: GroupId,
        responseHandler: (Group) -> T
    ): T {
        return responseHandler(groupQueryRepository.get(groupId))
    }
}