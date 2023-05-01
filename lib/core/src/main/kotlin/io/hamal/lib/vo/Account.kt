package io.hamal.lib.vo

import io.hamal.lib.util.SnowflakeId
import io.hamal.lib.vo.base.DomainId
import io.hamal.lib.vo.base.DomainIdSerializer
import kotlinx.serialization.Serializable

@Serializable(with = AccountId.Serializer::class)
class AccountId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))

    internal object Serializer : DomainIdSerializer<AccountId>(::AccountId)
}
