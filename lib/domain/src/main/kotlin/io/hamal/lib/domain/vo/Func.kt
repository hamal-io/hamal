package io.hamal.lib.domain.vo

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.vo.base.DomainId
import io.hamal.lib.domain.vo.base.DomainIdSerializer
import io.hamal.lib.domain.vo.base.DomainName
import io.hamal.lib.domain.vo.base.DomainNameSerializer
import kotlinx.serialization.Serializable

@Serializable(with = FuncId.Serializer::class)
class FuncId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))

    internal object Serializer : DomainIdSerializer<FuncId>(::FuncId)
}


@Serializable(with = FuncName.Serializer::class)
class FuncName(override val value: String) : DomainName() {
    internal object Serializer : DomainNameSerializer<FuncName>(::FuncName)
}
