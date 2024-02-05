package io.hamal.repository.record.topic

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.LogTopicId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordAdapter
import io.hamal.repository.record.RecordSequence
import io.hamal.repository.record.RecordedAt

sealed class TopicRecord(
    @Transient
    override var recordSequence: RecordSequence? = null,
    @Transient
    override var recordedAt: RecordedAt? = null
) : Record<TopicId>() {
    internal object Adapter : RecordAdapter<TopicRecord>(
        listOf(
            TopicGroupCreatedRecord::class,
            TopicInternalCreatedRecord::class
        )
    )
}

data class TopicGroupCreatedRecord(
    override val cmdId: CmdId,
    override val entityId: TopicId,
    val name: TopicName,
    val logTopicId: LogTopicId,
    val groupId: GroupId
) : TopicRecord()


data class TopicInternalCreatedRecord(
    override val cmdId: CmdId,
    override val entityId: TopicId,
    val name: TopicName,
    val logTopicId: LogTopicId,
    val groupId: GroupId
) : TopicRecord()
