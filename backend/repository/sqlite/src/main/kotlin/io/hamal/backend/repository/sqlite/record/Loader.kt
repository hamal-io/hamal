package io.hamal.backend.repository.sqlite.record

import io.hamal.backend.repository.record.Record
import io.hamal.backend.repository.record.RecordSequence
import io.hamal.lib.domain.vo.base.DomainId
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

@OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
class RecordLoader<ID : DomainId, RECORD : Record<ID>>(
    private val recordClass: KClass<RECORD>,
) {
    fun loadAll(tx: RecordTransaction<ID, RECORD, *>, id: ID): List<RECORD> {
        return tx.executeQuery(
            """
        SELECT 
            sequence,
            data
        FROM records 
            WHERE entity_id = :entityId 
        ORDER BY sequence;
    """.trimIndent()
        ) {
            query {
                set("entityId", id)
            }
            map { rs ->
                protobuf.decodeFromByteArray(recordClass.serializer(), rs.getBytes("data")).apply {
                    sequence = RecordSequence(rs.getInt("sequence"))
                }
            }
        }
    }


}