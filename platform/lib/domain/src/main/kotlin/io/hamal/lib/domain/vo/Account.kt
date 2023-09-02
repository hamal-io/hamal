package io.hamal.lib.domain.vo

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.*
import kotlinx.serialization.Serializable

@Serializable(with = AccountId.Serializer::class)
class AccountId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    internal object Serializer : DomainIdSerializer<AccountId>(::AccountId)
}

@Serializable(with = AccountName.Serializer::class)
class AccountName(override val value: String) : DomainName() {
    internal object Serializer : DomainNameSerializer<AccountName>(::AccountName)
}

@Serializable
class AccountEmail(override val value: String) : ValueObject.ComparableImpl<String>()

@Serializable
class AccountPassword(override val value: String) : ValueObject.ComparableImpl<String>()



