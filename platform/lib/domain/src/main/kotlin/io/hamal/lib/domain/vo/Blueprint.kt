package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.DomainName
import io.hamal.lib.common.domain.DomainNameSerializer
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.base.InputsSerializer
import io.hamal.lib.domain.vo.base.MapValueObject
import io.hamal.lib.kua.type.MapType
import kotlinx.serialization.Serializable


@Serializable(with = BlueprintId.Serializer::class)
class BlueprintId(override val value: SnowflakeId) : SerializableDomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))

    internal object Serializer : SerializableDomainIdSerializer<BlueprintId>(::BlueprintId)
}

@Serializable(with = BlueprintName.Serializer::class)
class BlueprintName(override val value: String) : DomainName() {
    internal object Serializer : DomainNameSerializer<BlueprintName>(::BlueprintName)
}

@Serializable(with = BlueprintInputs.Serializer::class)
class BlueprintInputs(override val value: MapType = MapType()) : MapValueObject() {
    internal object Serializer : InputsSerializer<BlueprintInputs>(::BlueprintInputs)
}