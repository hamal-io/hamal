package io.hamal.lib.domain.vo

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.DomainId
import io.hamal.lib.common.domain.DomainIdSerializer
import io.hamal.lib.common.domain.DomainName
import io.hamal.lib.common.domain.DomainNameSerializer
import io.hamal.lib.domain.vo.base.Inputs
import io.hamal.lib.domain.vo.base.InputsSerializer
import io.hamal.lib.kua.type.TableType
import kotlinx.serialization.Serializable

@Serializable(with = NamespaceId.Serializer::class)
class NamespaceId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))

    internal object Serializer : DomainIdSerializer<NamespaceId>(::NamespaceId)
}


@Serializable(with = NamespaceName.Serializer::class)
class NamespaceName(override val value: String) : DomainName() {
    internal object Serializer : DomainNameSerializer<NamespaceName>(::NamespaceName)
}

@Serializable(with = NamespaceInputs.Serializer::class)
class NamespaceInputs(override val value: TableType = TableType()) : Inputs() {
    internal object Serializer : InputsSerializer<NamespaceInputs>(::NamespaceInputs)
}
