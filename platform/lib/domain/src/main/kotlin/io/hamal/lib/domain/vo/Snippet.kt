package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.DomainName
import io.hamal.lib.common.domain.DomainNameSerializer
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.base.Inputs
import io.hamal.lib.domain.vo.base.InputsSerializer
import io.hamal.lib.kua.type.MapType
import kotlinx.serialization.Serializable


@Serializable(with = SnippetId.Serializer::class)
class SnippetId(override val value: SnowflakeId) : SerializableDomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))

    internal object Serializer : SerializableDomainIdSerializer<SnippetId>(::SnippetId)
}

@Serializable(with = SnippetName.Serializer::class)
class SnippetName(override val value: String) : DomainName() {
    internal object Serializer : DomainNameSerializer<SnippetName>(::SnippetName)
}

@Serializable(with = SnippetInputs.Serializer::class)
class SnippetInputs(override val value: MapType = MapType()) : Inputs() {
    internal object Serializer : InputsSerializer<SnippetInputs>(::SnippetInputs)
}