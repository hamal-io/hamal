package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.StringValueObjectSerializer
import io.hamal.lib.common.domain.ValueObjectString
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

@Serializable(with = Web3Address.Serializer::class)
class Web3Address(override val value: String) : ValueObjectString() {
    internal object Serializer : StringValueObjectSerializer<Web3Address>(::Web3Address)
}

@Serializable(with = Web3Challenge.Serializer::class)
class Web3Challenge(override val value: String) : ValueObjectString() {
    internal object Serializer : StringValueObjectSerializer<Web3Challenge>(::Web3Challenge)
}

@Serializable(with = Web3Signature.Serializer::class)
class Web3Signature(override val value: String) : ValueObjectString() {
    internal object Serializer : StringValueObjectSerializer<Web3Signature>(::Web3Signature)
}

enum class AccountType {
    Anonymous,
    Root,
    Runner,
    Service,
    User
}