package io.hamal.repository.record.account

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.serialization.JsonAdapter
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.AccountType
import io.hamal.lib.domain.vo.PasswordSalt
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordSequence
import io.hamal.repository.record.RecordedAt
import java.lang.reflect.Type

sealed class AccountRecord(
    @Transient
    override var recordSequence: RecordSequence? = null,
    @Transient
    override var recordedAt: RecordedAt? = null
) : Record<AccountId>() {

    internal object Adapter : JsonAdapter<AccountRecord> {
        override fun serialize(src: AccountRecord, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return context.serialize(src)
        }

        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): AccountRecord {
            return when (val kuaType = json.asJsonObject.get("recordType").asString) {
                AccountCreatedRecord::class.java.simpleName -> context.deserialize(json, AccountRecord::class.java)
                AccountConvertedRecord::class.java.simpleName -> context.deserialize(
                    json,
                    AccountConvertedRecord::class.java
                )

                else -> throw IllegalArgumentException("Expected True or False, but got $kuaType")
            }
        }
    }
}

data class AccountCreatedRecord(
    override val entityId: AccountId,
    override val cmdId: CmdId,
    val salt: PasswordSalt,
    val type: AccountType
) : AccountRecord()

data class AccountConvertedRecord(
    override val entityId: AccountId,
    override val cmdId: CmdId,
) : AccountRecord()