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

    internal object Serializer : SerializableDomainIdSerializer<AccountId>(::AccountId)
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

@Serializable(with = Web3Address.Serializer::class)
class Web3Address(override val value: String) : StringValueObject() {
    internal object Serializer : StringValueObjectSerializer<Web3Address>(::Web3Address)
}

@Serializable(with = Web3Challenge.Serializer::class)
class Web3Challenge(override val value: String) : StringValueObject() {
    internal object Serializer : StringValueObjectSerializer<Web3Challenge>(::Web3Challenge)
}

@Serializable(with = Web3Signature.Serializer::class)
class Web3Signature(override val value: String) : StringValueObject() {
    internal object Serializer : StringValueObjectSerializer<Web3Signature>(::Web3Signature)
}

enum class AccountType {
    Anonymous,
    Root,
    Runner,
    Service,
    User
}