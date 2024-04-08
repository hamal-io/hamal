package com.nyanbot.repository.impl.flow

import com.nyanbot.RecordAdapter
import com.nyanbot.repository.FlowId
import com.nyanbot.repository.record.Record
import com.nyanbot.repository.record.RecordSequence
import com.nyanbot.repository.record.RecordedAt

sealed class FlowRecord(
    @Transient
    override var recordSequence: RecordSequence? = null,
    @Transient
    override var recordedAt: RecordedAt? = null
) : Record<FlowId>() {

    internal object Adapter : RecordAdapter<FlowRecord>(
        listOf(
            Created::class,
        )
    )

    data class Created(
        override val entityId: FlowId,
    ) : FlowRecord()

}

