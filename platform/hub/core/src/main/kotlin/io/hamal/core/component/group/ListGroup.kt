package io.hamal.core.component.group

import io.hamal.repository.api.Group
import io.hamal.repository.api.GroupQueryRepository
import org.springframework.stereotype.Component

@Component
class ListGroup(private val groupQueryRepository: GroupQueryRepository) {
    operator fun <T : Any> invoke(
        query: GroupQueryRepository.GroupQuery,
        responseHandler: (List<Group>) -> T
    ): T {
        return responseHandler(groupQueryRepository.list(query))
    }
}