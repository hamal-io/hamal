package io.hamal.lib.domain.vo

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.*
import kotlinx.serialization.Serializable

@Serializable(with = AccountId.Serializer::class)
class AccountId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))

    internal object Serializer : DomainIdSerializer<AccountId>(::AccountId)
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