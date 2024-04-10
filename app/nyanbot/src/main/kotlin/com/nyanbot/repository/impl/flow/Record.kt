package com.nyanbot.repository.impl.flow

import com.nyanbot.RecordAdapter
import com.nyanbot.repository.Flow
import com.nyanbot.repository.FlowId
import com.nyanbot.repository.FlowName
import com.nyanbot.repository.FlowTrigger
import com.nyanbot.repository.record.Record
import com.nyanbot.repository.record.RecordSequence
import com.nyanbot.repository.record.RecordedAt
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.TriggerId

sealed class FlowRecord(
    @Transient
    override var recordSequence: RecordSequence? = null,
    @Transient
    override var recordedAt: RecordedAt? = null
) : Record<FlowId>() {

    internal object Adapter : RecordAdapter<FlowRecord>(
        listOf(
            Created::class,
            SetActive::class,
            SetInactive::class
        )
    )

    data class Created(
        override val entityId: FlowId,
        val name: FlowName,
        val flowTrigger: FlowTrigger,
        val accountId: AccountId,
        val namespaceId: NamespaceId?,
        val funcId: FuncId?,
        val triggerId: TriggerId?
    ) : FlowRecord()

    data class SetActive(
        override val entityId: FlowId
    ) : FlowRecord()


    data class SetInactive(
        override val entityId: FlowId
    ) : FlowRecord()

}

