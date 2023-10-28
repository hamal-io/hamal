package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.DomainName
import io.hamal.lib.common.domain.DomainNameSerializer
import io.hamal.lib.common.domain.StringValueObject
import io.hamal.lib.common.domain.StringValueObjectSerializer
import io.hamal.lib.common.snowflake.SnowflakeId
import kotlinx.serialization.Serializable

@Serializable(with = AccountId.Serializer::class)
class AccountId(override val value: SnowflakeId) : SerializableDomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))

    internal object Serializer : DomainIdSerializer<AccountId>(::AccountId)

    companion object {
        val root = AccountId(1)
    }
}

@Serializable(with = AccountName.Serializer::class)
class AccountName(override val value: String) : DomainName() {
    internal object Serializer : DomainNameSerializer<AccountName>(::AccountName)
}

@Serializable(with = AccountEmail.Serializer::class)
class AccountEmail(override val value: String) : StringValueObject() {
    //FIXME validate email
    internal object Serializer : StringValueObjectSerializer<AccountEmail>(::AccountEmail)
}

enum class AccountType {
    Enjoyer,
    Root,
    Runner
}