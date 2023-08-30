package io.hamal.repository.api.log

import io.hamal.lib.domain.vo.TopicEntryId
import io.hamal.lib.domain.vo.TopicEntryPayload

data class TopicEntry(
    val id: TopicEntryId,
    val payload: TopicEntryPayload
)